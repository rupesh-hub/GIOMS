package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.office.OfficeType;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeSavePojo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OfficeConverter extends AbstractConverter<OfficePojo, Office> {
    @Autowired
    ModelMapper modelMapper;

    @Override
    public Office toEntity(OfficePojo dto) {
        Office office = new Office();
        modelMapper.map(dto , office);
      return office;
    }
    public Office toOfficeEntity(OfficeSavePojo dto) {
        Office office = new Office();
        modelMapper.map(dto , office);
        modelMapper.getConfiguration().setSkipNullEnabled(Boolean.TRUE);

        if(dto.getParentCode()!= null) {
            Office parentOffice = new Office();
            parentOffice.setCode(dto.getParentCode());
            office.setParent(parentOffice);
        }

        if(dto.getOfficeTypeId() != null) {
            office.setOfficeType(new OfficeType(dto.getOfficeTypeId()));
        }
        if(dto.getOrganisationTypeId() != null) {
            office.setOrganisationType(new OrganisationType(dto.getOrganisationTypeId()));
        }
      return office;
    }


    @Override
    public OfficePojo toDto(Office office) {
        OfficePojo pojo = new OfficePojo();
        pojo.setNameEn(office.getNameEn());
        pojo.setCode(office.getCode());
        pojo.setNameNp(office.getNameNp());
        return pojo;
    }
}
