package com.gerp.usermgmt.converter.organiztion.orgtransfer;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.employee.EmployeeServiceStatus;
import com.gerp.usermgmt.pojo.organization.employee.FunctionalDesignationPojo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FunctionalDesignationConverter extends AbstractConverter<FunctionalDesignationPojo, FunctionalDesignation> {
    @Autowired
    ModelMapper modelMapper;

    @Override
    public FunctionalDesignation toEntity(FunctionalDesignationPojo dto) {
        FunctionalDesignation functionalDesignation = new FunctionalDesignation();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, functionalDesignation);
        EmployeeServiceStatus es = new EmployeeServiceStatus();
        es.setCode(dto.getEmployeeServiceStatusCode());
        return functionalDesignation;
    }

    public FunctionalDesignation toUpdateEntity(FunctionalDesignationPojo dto, FunctionalDesignation functionalDesignation) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, functionalDesignation);
        EmployeeServiceStatus es = new EmployeeServiceStatus();
        es.setCode(dto.getEmployeeServiceStatusCode());
        return functionalDesignation;
    }

    @Override
    public FunctionalDesignation toEntity(FunctionalDesignationPojo dto, FunctionalDesignation entity) {
        return super.toEntity(dto, entity);
    }

    @Override
    public FunctionalDesignationPojo toDto(FunctionalDesignation entity) {
        return super.toDto(entity);
    }

    @Override
    public List<FunctionalDesignation> toEntity(List<FunctionalDesignationPojo> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<FunctionalDesignationPojo> toDto(List<FunctionalDesignation> entityList) {
        return super.toDto(entityList);
    }
}
