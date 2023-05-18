package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.HelperUtil;
import com.gerp.usermgmt.converter.organiztion.employee.EmployeeConverter;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.employee.*;
import com.gerp.usermgmt.pojo.organization.office.SectionDesignationPojo;
import com.gerp.usermgmt.repo.designation.PositionRepo;
import com.gerp.usermgmt.repo.employee.ServiceGroupRepo;
import com.gerp.usermgmt.services.organization.designation.FunctionalDesignationService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.office.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SectionDesignationConverter extends AbstractConverter<SectionDesignationPojo, SectionDesignation> {
    @Autowired
    private SectionService sectionService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FunctionalDesignationService functionalDesignationService;

    @Autowired
    private EmployeeConverter employeeConverter;

    @Autowired
    private SectionConverter sectionConverter;

    @Autowired
    private CustomMessageSource customMessageSource;

    private final ServiceGroupRepo serviceGroupRepo;
    private final PositionRepo positionRepo;

    public SectionDesignationConverter(ServiceGroupRepo serviceGroupRepo, PositionRepo positionRepo) {
        this.serviceGroupRepo = serviceGroupRepo;
        this.positionRepo = positionRepo;
    }

    @Override
    public SectionDesignation toEntity(SectionDesignationPojo dto) {
        SectionDesignation sectionDesignation = new SectionDesignation();
        sectionDesignation.setId(dto.getId());

        if (!ObjectUtils.isEmpty(dto.getPisCode())) {
            Employee employee = employeeService.detail(dto.getPisCode());
            sectionDesignation.setEmployee(employee);
        }


        FunctionalDesignation functionalDesignation = functionalDesignationService.findById(
                dto.getFunctionalDesignationCode()
        );
        if (ObjectUtils.isEmpty(functionalDesignation)) {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "designation"));
        }
        sectionDesignation.setFunctionalDesignation(functionalDesignation);

        SectionSubsection sectionSubsection = sectionService.findById(dto.getSectionId());
        if (ObjectUtils.isEmpty(sectionSubsection)) {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "section"));
        }
        sectionDesignation.setSectionSubsection(sectionSubsection);

        if (!ObjectUtils.isEmpty(dto.getPositionCode() )){
           Optional<Position> positionOptional = positionRepo.findById(dto.getPositionCode());
           if (!positionOptional.isPresent()){
               throw new RuntimeException(customMessageSource.get("error.doesn't.exist","position"));
           }
           sectionDesignation.setPosition(positionOptional.orElse(new Position()));
        }
        String code = getSectionCode(dto.getSubGroupCode(),dto.getGroupCode(), dto.getServiceCode());
        if (!ObjectUtils.isEmpty(code)){
            Optional<Service> serviceOptional = serviceGroupRepo.findById(code);
            if (!serviceOptional.isPresent()){
                throw new RuntimeException(customMessageSource.get("error.doesn't.exist","service"));
            }
            sectionDesignation.setService(serviceOptional.orElse(new Service()));
        }
        sectionDesignation.setDisabled(Boolean.FALSE);
        return sectionDesignation;
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

    @Override
    public SectionDesignation toEntity(SectionDesignationPojo dto, SectionDesignation entity) {
        return super.toEntity(dto, entity);
    }

    @Override
    public SectionDesignationPojo toDto(SectionDesignation entity) {
        SectionDesignationPojo dto = new SectionDesignationPojo();
        dto.setId(entity.getId());
        dto.setIsActive(entity.isActive());
        dto.setEmployee(employeeConverter.toDto(entity.getEmployee()));
        FunctionalDesignation f = entity.getFunctionalDesignation();
        dto.setFunctionalDesignation(IdNamePojo.builder().code(f.getCode())
                .name(f.getNameEn()).nameN(f.getNameNp()).build());
        dto.setSection(sectionConverter.toDto(entity.getSectionSubsection()));


        return dto;
    }

    @Override
    public List<SectionDesignation> toEntity(List<SectionDesignationPojo> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return null;
        }
        return dtoList.parallelStream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<SectionDesignationPojo> toDto(List<SectionDesignation> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
