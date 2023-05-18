package com.gerp.usermgmt.converter.transfer;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.transfer.*;
import com.gerp.usermgmt.pojo.transfer.ChecklistPojo;
import com.gerp.usermgmt.pojo.transfer.PreviousWorkDetailPojo;
import com.gerp.usermgmt.pojo.transfer.TransferRequestPojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentMasterResponsePojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentSavePojo;
import com.gerp.usermgmt.token.TokenProcessorService;
import com.gerp.usermgmt.util.DocumentUtil;
import com.gerp.usermgmt.validator.EntityValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransferRequestConverter extends AbstractConverter<TransferRequestPojo, TransferRequest> {

    private final TokenProcessorService tokenProcessorService;
    private final EntityValidator entityValidator;

    private final DocumentUtil documentUtil;

    public TransferRequestConverter(TokenProcessorService tokenProcessorService, EntityValidator entityValidator, DocumentUtil documentUtil) {
        this.tokenProcessorService = tokenProcessorService;
        this.entityValidator = entityValidator;
        this.documentUtil = documentUtil;
    }

    @Override
    public TransferRequest toEntity(TransferRequestPojo dto) {
        TransferRequest transferRequest = new TransferRequest();
//        entityValidator.validateEntities(dto.getNewPositionCode(), dto.getNewServiceCode(), dto.getNewDesignationCode());
        dto.getNewOfficeCodes().forEach( entityValidator::getOffice);
        transferRequest.setEmployeePsCode(tokenProcessorService.getPisCode());
        transferRequest.setRequestedDateEn(dto.getRequestedDateEn());
        transferRequest.setRequestedDateNp(dto.getRequestedDateNp());
//        transferRequest.setDesignationCode(dto.getNewDesignationCode());
//        transferRequest.setPositionCode(dto.getNewPositionCode());
//        transferRequest.setServiceCode(dto.getNewServiceCode());
        transferRequest.setRemark(dto.getRemarks());
        transferRequest.setIsSubmitted(false);
        transferRequest.setRequestReviewOfficeCode(dto.getRequestedOfficeCode());
        transferRequest.setAttendanceDays(dto.getAttendanceDays());
        transferRequest.setPreviousWorkDetailsList(dto.getPreviousWorkDetail().stream().map(this::toEntityPreviousWorkDetail).collect(Collectors.toList()));
        transferRequest.setRequestedOfficeList(dto.getNewOfficeCodes().stream().map(pojo->{
            RequestedOffice requestedOffice = new RequestedOffice();
            requestedOffice.setOfficeCode(pojo);
            return requestedOffice;
        }).collect(Collectors.toList()));
       if (dto.getDocument() != null){
            processDocument(dto.getDocument(),transferRequest);
        }
       transferRequest.setCheckLists(dto.getChecklists().stream().map(this::toEntityRequestChecklist).collect(Collectors.toList()));
        return transferRequest;
    }

    private EmployeeTransferRequestAndCheckList toEntityRequestChecklist(ChecklistPojo checklistPojo){
        EmployeeTransferRequestAndCheckList employeeTransferRequestAndCheckList = new EmployeeTransferRequestAndCheckList();
        employeeTransferRequestAndCheckList.setChecklistId(checklistPojo.getCheckListId());
        employeeTransferRequestAndCheckList.setStatus(checklistPojo.getStatus());
        return employeeTransferRequestAndCheckList;
    }
    private PreviousWorkDetails toEntityPreviousWorkDetail(PreviousWorkDetailPojo dto) {
        PreviousWorkDetails previousWorkDetails = new PreviousWorkDetails();
        previousWorkDetails.setDesignationCode(dto.getOldDesignationCode());
        previousWorkDetails.setServiceCode(dto.getOldServiceCode());
        previousWorkDetails.setFromDateEn(dto.getFromDateEn());
        previousWorkDetails.setFromDateNp(dto.getFromDateNp());
        previousWorkDetails.setPositionCode(dto.getOldPositionCode());
        previousWorkDetails.setOfficeCode(dto.getOldOfficeCode());
        previousWorkDetails.setRegionCode(dto.getOldRegionCode());
        previousWorkDetails.setToDateEn(dto.getToDateEn());
        previousWorkDetails.setToDateNp(dto.getToDateNp());
        return previousWorkDetails;
    }
    private void processDocument(List<MultipartFile> document, TransferRequest transferRequest) {
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
                            x-> new TransferRequestDocuments().builder()
                                    .documentId(x.getId())
                                    .name(x.getName())
                                    .type("General")
                                    .size(x.getSizeKB()+ "")
                                    .build()
                    ).collect(Collectors.toList())
            );
        }
    }

}
