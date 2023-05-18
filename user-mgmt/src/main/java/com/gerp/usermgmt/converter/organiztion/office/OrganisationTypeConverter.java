package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.pojo.organization.office.OrganisationTypePojo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrganisationTypeConverter extends AbstractConverter<OrganisationTypePojo, OrganisationType> {

    private final ModelMapper modelMapper;

    public OrganisationTypeConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public OrganisationType toEntity(OrganisationTypePojo dto) {
        OrganisationType organisationType = new OrganisationType();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto , organisationType);
        return organisationType;
    }

    public OrganisationType toUpdateEntity(OrganisationTypePojo dto, OrganisationType organisationType) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto , organisationType);
        return organisationType;
    }

    @Override
    public OrganisationType toEntity(OrganisationTypePojo dto, OrganisationType entity) {
        return super.toEntity(dto, entity);
    }

    @Override
    public OrganisationTypePojo toDto(OrganisationType entity) {
        return super.toDto(entity);
    }

    @Override
    public List<OrganisationType> toEntity(List<OrganisationTypePojo> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<OrganisationTypePojo> toDto(List<OrganisationType> entityList) {
        return super.toDto(entityList);
    }
}
