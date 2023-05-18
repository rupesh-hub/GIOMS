package com.gerp.usermgmt.converter.organiztion.administrative;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.usermgmt.model.administrative.AdministrativeBody;
import com.gerp.usermgmt.pojo.organization.administrative.AdministrativeBodyPojo;
import com.gerp.usermgmt.repo.AdministrationLevelRepo;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdministrativeBodyConverter extends AbstractConverter
        <AdministrativeBodyPojo, AdministrativeBody> {

    @Autowired
    private AdministrationLevelRepo administrationLevelRepo;

    @Override
    public AdministrativeBody toEntity(AdministrativeBodyPojo dto) {
        AdministrativeBody entity = new AdministrativeBody();
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(entity, dto);
            entity.setAdministrationLevel(dto.getAdministrativeLevel().getId() == null ? null : administrationLevelRepo.findById(dto.getAdministrativeLevel().getId()).get());
        } catch (Exception e) {
            throw new RuntimeException("id doesnot exists");
        }
       return entity;
    }

    @Override
    public AdministrativeBody toEntity(AdministrativeBodyPojo dto, AdministrativeBody entity) {
        return super.toEntity(dto, entity);
    }

    @Override
    public AdministrativeBodyPojo toDto(AdministrativeBody entity) {
        return super.toDto(entity);
    }

    @Override
    public List<AdministrativeBodyPojo> toDto(List<AdministrativeBody> entityList) {
        return super.toDto(entityList);
    }
}
