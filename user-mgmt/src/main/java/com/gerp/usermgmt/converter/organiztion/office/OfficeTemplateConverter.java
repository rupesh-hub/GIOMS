package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.shared.pojo.OfficeMinimalPojo1;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.office.OfficeTemplate;
import com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class OfficeTemplateConverter extends AbstractConverter<OfficeTemplatePojo, OfficeTemplate> {
    @Override
    public OfficeTemplate toEntity(OfficeTemplatePojo dto) {
        OfficeTemplate officeTemplate;
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        officeTemplate = modelMapper.map(dto , OfficeTemplate.class);
        officeTemplate.setOffice(new Office(dto.getOfficeCode()));
        officeTemplate.setIsQrTemplate(dto.getIsQrTemplate());
        officeTemplate.setActive(dto.getIsActive());
        return officeTemplate;
    }


    @Override
    public OfficeTemplate toEntity(OfficeTemplatePojo dto, OfficeTemplate entity) {
        return super.toEntity(dto, entity);
    }

    @Override
    public OfficeTemplatePojo toDto(OfficeTemplate entity) {
        OfficeTemplatePojo officeTemplate;
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        officeTemplate = modelMapper.map(entity , OfficeTemplatePojo.class);
        officeTemplate.setIsActive(entity.getActive());
        if(entity.getOffice().getId() == null) {
            throw new ServiceValidationException("Office is not added for this template");
        }
            officeTemplate.setOffice(OfficeMinimalPojo1.builder().name(entity.getOffice().getNameEn()).nameN(entity.getOffice().getNameNp()).code(entity.getOffice().getCode()).build());
        return officeTemplate;
    }

    @Override
    public List<OfficeTemplate> toEntity(List<OfficeTemplatePojo> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<OfficeTemplatePojo> toDto(List<OfficeTemplate> entityList) {
        return super.toDto(entityList);
    }
}
