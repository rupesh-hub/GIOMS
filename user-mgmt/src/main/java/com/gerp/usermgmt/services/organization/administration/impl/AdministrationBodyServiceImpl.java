package com.gerp.usermgmt.services.organization.administration.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.usermgmt.converter.organiztion.administrative.AdministrativeBodyConverter;
import com.gerp.usermgmt.mapper.organization.AdministrativeBodyMapper;
import com.gerp.usermgmt.model.administrative.AdministrativeBody;
import com.gerp.usermgmt.pojo.organization.administrative.AdministrativeBodyPojo;
import com.gerp.usermgmt.repo.AdministrativeBodyRepo;
import com.gerp.usermgmt.services.organization.administration.AdministrationBodyService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AdministrationBodyServiceImpl extends GenericServiceImpl<AdministrativeBody, Integer> implements AdministrationBodyService {
    private final AdministrativeBodyRepo administrativeBodyRepo;
    private final AdministrativeBodyConverter administrativeBodyConverter;
    private final AdministrativeBodyMapper administrativeBodyMapper;

    public AdministrationBodyServiceImpl(AdministrativeBodyRepo administrativeBodyRepo,
                                         AdministrativeBodyConverter administrativeBodyConverter,
                                         AdministrativeBodyMapper administrativeBodyMapper) {
        super(administrativeBodyRepo);
        this.administrativeBodyRepo = administrativeBodyRepo;
        this.administrativeBodyConverter = administrativeBodyConverter;
        this.administrativeBodyMapper = administrativeBodyMapper;
    }

    @Override
    public AdministrativeBody update(AdministrativeBodyPojo pojo) {
        AdministrativeBody update = administrativeBodyRepo.findById(pojo.getId()).get();
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, administrativeBodyConverter.toEntity(pojo));
        } catch (Exception e) {
            throw new RuntimeException("id does not exists");
        }
        return administrativeBodyRepo.save(update);
    }

    @Override
    public AdministrativeBodyPojo getById(Integer id) {
        return administrativeBodyMapper.getById(id);
    }

    @Override
    public List<AdministrativeBodyPojo> findAllList() {
        return administrativeBodyMapper.getAll();
    }


}
