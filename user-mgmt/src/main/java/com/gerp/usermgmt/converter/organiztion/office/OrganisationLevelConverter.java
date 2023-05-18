package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.office.OrganizationLevel;
import com.gerp.usermgmt.pojo.organization.office.OrganizationLevelPojo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationLevelConverter extends AbstractConverter<OrganizationLevelPojo, OrganizationLevel> {
    @Autowired
    private  final ModelMapper modelMapper;

    public OrganisationLevelConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setSkipNullEnabled(Boolean.TRUE);
    }

    @Override
    public OrganizationLevel toEntity(OrganizationLevelPojo dto) {
        OrganizationLevel organizationLevel = new OrganizationLevel();
        modelMapper.map(dto, organizationLevel);
        return organizationLevel;
    }


    @Override
    public OrganizationLevelPojo toDto(OrganizationLevel organizationLevel) {
        OrganizationLevelPojo organizationLevelDto = new OrganizationLevelPojo();
        modelMapper.map(organizationLevel, organizationLevelDto);
        return organizationLevelDto;
    }
}
