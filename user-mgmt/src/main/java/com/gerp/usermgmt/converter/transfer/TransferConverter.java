package com.gerp.usermgmt.converter.transfer;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.shared.utils.HelperUtil;
import com.gerp.usermgmt.constant.TransferConstant;
import com.gerp.usermgmt.mapper.organization.OfficeMapper;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.transfer.TransferConfig;
import com.gerp.usermgmt.model.transfer.TransferDocuments;
import com.gerp.usermgmt.model.transfer.TransferHistory;
import com.gerp.usermgmt.model.transfer.TransferRemarks;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import com.gerp.usermgmt.pojo.transfer.TransferConfigPojo;
import com.gerp.usermgmt.pojo.transfer.TransferPojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentMasterResponsePojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentSavePojo;
import com.gerp.usermgmt.repo.address.DistrictRepo;
import com.gerp.usermgmt.repo.office.OfficeRepo;
import com.gerp.usermgmt.token.TokenProcessorService;
import com.gerp.usermgmt.util.DocumentUtil;
import com.gerp.usermgmt.validator.EntityValidator;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransferConverter extends AbstractConverter<TransferPojo,TransferHistory> {

    private final EntityValidator entityValidator;
    private final CustomMessageSource customMessageSource;
    private final TokenProcessorService tokenProcessorService;
    private final DocumentUtil documentUtil;
    private final OfficeMapper officeMapper;


    public TransferConverter(EntityValidator entityValidator, CustomMessageSource customMessageSource, TokenProcessorService tokenProcessorService, DocumentUtil documentUtil, OfficeMapper officeMapper) {
        this.entityValidator = entityValidator;
        this.customMessageSource = customMessageSource;
        this.tokenProcessorService = tokenProcessorService;
        this.documentUtil = documentUtil;

        this.officeMapper = officeMapper;
    }

    @Override
    public TransferHistory toEntity(TransferPojo dto, TransferHistory entity) {
        String fromCode = getSectionCode(dto.getFromSubGroupCode(),dto.getFromGroupCode(), dto.getFromServiceCode());
        String toCode = getSectionCode(dto.getToSubGroupCode(),dto.getToGroupCode(), dto.getToServiceCode());

        entityValidator.validateEntities(dto.getFromPositionCode(),fromCode,dto.getFromDesignationCode(),dto.getFromOfficeCode());
        entityValidator.validateEntities(dto.getToPositionCode(),toCode,dto.getToDesignationCode(),dto.getToOfficeCode());
        entityValidator.getEmployee(dto.getEmployeePisCode());

//        if (dto.getExpectedDepartureDateEn()== null || dto.getExpectedDepartureDateEn().isBefore(LocalDate.now())){
//            throw new RuntimeException(customMessageSource.get(CrudMessages.invalidDateAfter,"Expected departure date","today"));
//        }
        entity.setApproverCode(dto.getApproverCode());
//        entity.setExpectedDepartureDateEn(dto.getExpectedDepartureDateEn());
//        entity.setExpectedDepartureDateNp(dto.getExpectedDepartureDateNp());
        entity.setFromDesignationCode(dto.getFromDesignationCode());
        entity.setFromOfficeCode(dto.getFromOfficeCode());
        entity.setFromPositionCode(dto.getFromPositionCode());
        entity.setTransferType(dto.getTransferType().toUpperCase());
        entity.setToDesignationCode(dto.getToDesignationCode());
        entity.setToOfficeCode(dto.getToOfficeCode());
        entity.setToPositionCode(dto.getToPositionCode());
        entity.setFromServiceCode(fromCode);
        entity.setFromSectionCode(dto.getFromSectionCode());
        entity.setToSectionCode(dto.getToSectionCode());
        entity.setToServiceCode(toCode);
        entity.setPisCode(dto.getEmployeePisCode());
        entity.setApproved(Status.P.getValueEnglish());
        if (dto.getRemarks() != null) {
            entity.setTransfer_remarks(toTransferRemarksEntity(dto.getRemarks()));
        }
        if (dto.getDocuments() != null){
            processDocument(dto.getDocuments(),entity);
        }
        return entity;
    }

    public List<TransferRemarks> toTransferRemarksEntity(String remarks) {
        List<TransferRemarks> transferRemarks = new ArrayList<>();
        TransferRemarks transferRemark = getTransferRemarks(remarks);
        transferRemarks.add(transferRemark);
        return transferRemarks;
    }

    public TransferRemarks getTransferRemarks(String remarks) {
        TransferRemarks transferRemark = new TransferRemarks();
        transferRemark.setPisCode(tokenProcessorService.getPisCode());
        transferRemark.setRemark(remarks);
        return transferRemark;
    }


    private String getSectionCode(String subGroup, String group , String service){
        if (!HelperUtil.isEmpty(subGroup)){
            return subGroup;
        }else if (!HelperUtil.isEmpty(group)){
            return group;
        }else{
            return service;
        }
    }

    private void processDocument(List<MultipartFile> document, TransferHistory transferRequest) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tags(Arrays.asList("transfer_mgmt"))
                        .type("1")
                        .build(),
                document
        );
        if(pojo!=null){
            transferRequest.setRelatedDocumentsList(
                    pojo.getDocuments().stream().map(
                            x-> new TransferDocuments().builder()
                                    .documentId(x.getId())
                                    .name(x.getName())
                                    .type("General")
                                    .size(x.getSizeKB()+"")
                                    .build()
                    ).collect(Collectors.toList())
            );
        }
    }

    @SneakyThrows
    public TransferConfigPojo convertToTransferConfigPojo(TransferConfig transferConfig) {
        TransferConfigPojo transferConfigPojo = new TransferConfigPojo();
        transferConfigPojo.setId(transferConfig.getId());
//        transferConfigPojo.setIsSaruwa(transferConfig.getType().equals(TransferConstant.SARUWA.toString()));
//        OfficePojo office =officeMapper.getOfficeByCode(transferConfig.getOfficeCode());
//
//        DetailPojo district = new DetailPojo();
//        district.setCode(office.getDistrict().getCode());
//        district.setNameNp(office.getDistrict().getNameN());
//        district.setNameEn(office.getDistrict().getName());
//        DetailPojo officeResponse = new DetailPojo();
//        officeResponse.setCode(office.getCode());
//        officeResponse.setNameEn(office.getNameEn());
//        officeResponse.setNameNp(office.getNameNp());
//        officeResponse.setDistrict(district);
//        transferConfigPojo.setOffice(officeResponse);

        transferConfigPojo.setMinisterCode(transferConfig.getMinisterCode());
//        transferConfigPojo.setOfficeCode(transferConfig.getOfficeCode());
        return transferConfigPojo;
    }
}
