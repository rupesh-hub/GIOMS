package com.gerp.usermgmt.services.organization.office.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.mapper.organization.OfficeMapper;
import com.gerp.usermgmt.model.office.OfficeGroup;
import com.gerp.usermgmt.model.office.OfficeGroupDetail;
import com.gerp.usermgmt.pojo.OfficeGroupDto;
import com.gerp.usermgmt.pojo.organization.office.ExternalOfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeGroupPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.repo.office.ExternalOfficeRepo;
import com.gerp.usermgmt.repo.office.OfficeGroupRepo;
import com.gerp.usermgmt.services.organization.office.OfficeGroupingService;
import com.gerp.usermgmt.token.TokenProcessorService;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class OfficeGroupingServiceImpl implements OfficeGroupingService {
    private final OfficeGroupRepo officeGroupRepo;
    private final ExternalOfficeRepo externalOfficeRepo;
    private final OfficeMapper officeMapper;
    @Autowired
    private final TokenProcessorService tokenProcessorService;
    private final CustomMessageSource customMessageSource;
    private static final String TYPE= "EXTERNAL_OFFICE";

    public OfficeGroupingServiceImpl(OfficeGroupRepo officeGroupRepo, ExternalOfficeRepo externalOfficeRepo, OfficeMapper officeMapper, TokenProcessorService tokenProcessorService, CustomMessageSource customMessageSource) {
        this.officeGroupRepo = officeGroupRepo;
        this.externalOfficeRepo = externalOfficeRepo;
        this.officeMapper = officeMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public int addOfficeGroup(OfficeGroupPojo officeGroupPojo) {
        OfficeGroupDetail officeGroupDetail = new OfficeGroupDetail();
        officeGroupDetail.setNameEn(officeGroupPojo.getNameEn());
        officeGroupDetail.setNameNp(officeGroupPojo.getNameNp());
        officeGroupDetail.setSavedByAdmin(tokenProcessorService.isOfficeAdmin());
        officeGroupDetail.setOfficeCode(tokenProcessorService.getOfficeCode());

        officeGroupDetail.setOfficeGroups(officeGroupPojo.getOffice().parallelStream().map(obj->{
            OfficeGroup officeGroup = new OfficeGroup();
            officeGroup.setCode(obj.getCode());
            officeGroup.setAddress(obj.getAddress());
            officeGroup.setPhoneNumber(obj.getPhoneNumber());
            officeGroup.setEmail(obj.getEmail());
            officeGroup.setSectionName(obj.getSectionName());
            officeGroup.setType(obj.getType());
            if (obj.getType().equalsIgnoreCase("others")){
                officeGroup.setCode(tokenProcessorService.getOfficeCode());
            }
            officeGroup.setNameEn(obj.getNameEn());
            officeGroup.setSectionSubSectionId(obj.getSectionSubSectionId());
            officeGroup.setNameNp(obj.getNameNp());
            officeGroup.setOrdering(obj.getOrder());
            return officeGroup;
        }).collect(Collectors.toList()));
        return officeGroupRepo.save(officeGroupDetail).getId();
    }

    @Override
    public List<OfficeGroupPojo> getOfficeGroup(String officeCode, String districtCode, String officeLevelCode) {
        List<OfficeGroupPojo> officeGroup = officeMapper.getOfficeGroup(tokenProcessorService.getUserId(), officeCode, districtCode, officeLevelCode,tokenProcessorService.getOfficeCode());
        officeGroup.parallelStream().forEach(obj->
            obj.setOfficePojos( obj.getOfficePojos().stream().map(obj1-> {
                if (obj1.getType()!=null && obj1.getType().equalsIgnoreCase("PISRegistered")){
                    OfficePojo officeByCode = officeMapper.getOfficeByCode(obj1.getCode());
                    officeByCode.setType(obj1.getType());
                    officeByCode.setOrder(obj1.getOrder());
                    officeByCode.setSection(obj1.getSection());
                    return officeByCode;
                }else {
                    return obj1;
                }
            }).sorted(Comparator.comparing(OfficePojo::getOrder,Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList())));
        return officeGroup;
    }

    @Override
    public int updateOfficeGroup(OfficeGroupPojo dto) {
        OfficeGroupDetail officeGroupDetail = getOfficeGroupDetail(dto.getId());
        if (!officeGroupDetail.getCreatedBy().equals(tokenProcessorService.getUserId())){
            throw new CustomException(customMessageSource.get(CrudMessages.notPermit,"उप्डेट"));
        }
        officeGroupDetail.setNameNp(dto.getNameNp());
        officeGroupDetail.setNameEn(dto.getNameEn());
            List<OfficeGroup> officeGroups = dto.getOffice().parallelStream().map(obj -> {
                OfficeGroup officeGroup = new OfficeGroup();
                officeGroup.setCode(obj.getCode());
                officeGroup.setEmail(obj.getEmail());
                officeGroup.setNameNp(obj.getNameNp());
                officeGroup.setNameEn(obj.getNameEn());
                officeGroup.setSectionName(obj.getSectionName());
                officeGroup.setSectionSubSectionId(obj.getSectionSubSectionId());
                officeGroup.setAddress(obj.getAddress());
                officeGroup.setPhoneNumber(obj.getPhoneNumber());
                officeGroup.setId(obj.getId());
                officeGroup.setOrdering(obj.getOrder());
                officeGroup.setType(obj.getType());
                return officeGroup;
            }).collect(Collectors.toList());
            officeGroupDetail.setOfficeGroups(officeGroups);
        officeGroupRepo.save(officeGroupDetail);
        officeGroupRepo.deleteRow();
        return dto.getId();
    }


    private OfficeGroupDetail getOfficeGroupDetail(int dto) {
        Optional<OfficeGroupDetail> byId = officeGroupRepo.findById(dto);
        if (!byId.isPresent()){
            throw new CustomException(customMessageSource.get(CrudMessages.notExist,"Id"));
        }
        return byId.orElse(new OfficeGroupDetail());
    }

    @Override
    public int deleteOfficeGroup(Integer id) {
       OfficeGroupDetail officeGroupDetail = getOfficeGroupDetail(id);
       if (!officeGroupDetail.getCreatedBy().equals(tokenProcessorService.getUserId())){
           throw new CustomException(customMessageSource.get(CrudMessages.notPermit,"Delete"));
       }
       officeGroupRepo.deleteById(id);
        return id;
    }

    @Override
    public OfficeGroupDto getOfficeGroup(Integer id) {
        return officeMapper.getOfficeGroupById(id);
    }

    @SneakyThrows
    @Override
    public int addExternalOfficeGroup(ExternalOfficePojo externalOfficePojo) {
        OfficeGroup officeGroup = new OfficeGroup();
        BeanUtils.copyProperties(officeGroup,externalOfficePojo);
        officeGroup.setType(TYPE);
        officeGroup.setCode(tokenProcessorService.getOfficeCode());
        externalOfficeRepo.save(officeGroup);
        return officeGroup.getId();
    }

    @Override
    public List<ExternalOfficePojo> getExternalOfficeGroup() {
        return officeMapper.findByType(TYPE,tokenProcessorService.getOfficeCode());
    }

    @Override
    public OfficeGroup getExternalOfficeById(int id) {
        return externalOfficeRepo.findById(id).orElse(new OfficeGroup());
    }
}
