package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.employee.SectionSubsection;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.pojo.organization.office.SectionPojo;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SectionConverter extends AbstractConverter<SectionPojo, SectionSubsection> {

    @Override
    public SectionSubsection toEntity(SectionPojo dto) {
        SectionSubsection sectionSubsection = new SectionSubsection();
        sectionSubsection.setCode(dto.getCode());

        sectionSubsection.setNameEn(dto.getNameEn());
        sectionSubsection.setNameNp(dto.getNameNp());
        sectionSubsection.setDartaCode(dto.getDartaCode());

        sectionSubsection.setRoomNo(dto.getRoomNo());
        sectionSubsection.setDefinedCode(dto.getDefinedCode());
        sectionSubsection.setChalaniCode(dto.getChalaniCode());
        sectionSubsection.setPhone(dto.getPhone());
        sectionSubsection.setFax(dto.getFax());
        sectionSubsection.setOrderNo(dto.getOrderNo());

        Office office = new Office();
        office.setCode(dto.getOfficeCode());
        sectionSubsection.setOffice(office);

        sectionSubsection.setActive(dto.getIsActive());
        sectionSubsection.setApproved(dto.getApproved());

        if(!ObjectUtils.isEmpty(dto.getParentId())){
            SectionSubsection parent = new SectionSubsection();
            parent.setId(dto.getParentId());
            sectionSubsection.setParent(parent);
        }

        return sectionSubsection;
    }


    @Override
    public SectionPojo toDto(SectionSubsection sectionSubsection) {
        SectionPojo sectionPojo = new SectionPojo();
        sectionPojo.setCode(sectionSubsection.getCode());
        sectionPojo.setId(sectionSubsection.getId());
        if(!ObjectUtils.isEmpty(sectionSubsection.getParent())){
            sectionPojo.setParentId(sectionSubsection.getParent().getId());
        }

        sectionPojo.setNameEn(sectionSubsection.getNameEn());
        sectionPojo.setNameNp(sectionSubsection.getNameNp());
        sectionPojo.setDartaCode(sectionSubsection.getDartaCode());

        sectionPojo.setRoomNo(sectionSubsection.getRoomNo());
        sectionPojo.setChalaniCode(sectionSubsection.getChalaniCode());
        sectionPojo.setPhone(sectionSubsection.getPhone());
        sectionPojo.setFax(sectionSubsection.getFax());

        sectionPojo.setIsActive(sectionSubsection.isActive());
        sectionPojo.setApproved(sectionSubsection.getApproved());
        sectionPojo.setOrderNo(sectionSubsection.getOrderNo());


        return sectionPojo;
    }
}
