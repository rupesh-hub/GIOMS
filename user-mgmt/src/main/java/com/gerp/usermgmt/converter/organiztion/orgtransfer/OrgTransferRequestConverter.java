package com.gerp.usermgmt.converter.organiztion.orgtransfer;


import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferRequest;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrgTransferRequestConverter extends AbstractConverter<OrgTransferRequestPojo, OrgTransferRequest> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public OrgTransferRequest toEntity(OrgTransferRequestPojo dto) {
        OrgTransferRequest orgTransferRequest= new OrgTransferRequest();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(dto , orgTransferRequest);
        orgTransferRequest.setTargetOffice(new Office(dto.getTargetOfficeCode()));
        return orgTransferRequest;
    }

    @Override
    public OrgTransferRequest toEntity(OrgTransferRequestPojo dto, OrgTransferRequest entity) {
        return super.toEntity(dto, entity);
    }

    @Override
    public OrgTransferRequestPojo toDto(OrgTransferRequest entity) {
        return super.toDto(entity);
    }

    @Override
    public List<OrgTransferRequest> toEntity(List<OrgTransferRequestPojo> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<OrgTransferRequestPojo> toDto(List<OrgTransferRequest> entityList) {
        return super.toDto(entityList);
    }
}
