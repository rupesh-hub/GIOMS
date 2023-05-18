package com.gerp.usermgmt.services.organization.office.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.usermgmt.mapper.organization.OrganizationLevelMapper;
import com.gerp.usermgmt.model.office.OrganizationLevel;
import com.gerp.usermgmt.pojo.organization.office.OrganizationLevelPojo;
import com.gerp.usermgmt.repo.office.OfficeLevelRepo;
import com.gerp.usermgmt.services.organization.office.OfficeLevelService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeLevelServiceImpl extends GenericServiceImpl<OrganizationLevel, Integer> implements OfficeLevelService {

    private final TokenProcessorService tokenProcessorService;
    private final OfficeLevelRepo officeLevelRepo;
    private final OrganizationLevelMapper organizationLevelMapper;
    public OfficeLevelServiceImpl(TokenProcessorService tokenProcessorService, OfficeLevelRepo officeLevelRepo, OrganizationLevelMapper organizationLevelMapper) {
        super(officeLevelRepo);
        this.tokenProcessorService = tokenProcessorService;
        this.officeLevelRepo = officeLevelRepo;
        this.organizationLevelMapper = organizationLevelMapper;
    }

    @Override
    public List<OrganizationLevelPojo> findAll() {
        return organizationLevelMapper.getAllOfficeLevel(tokenProcessorService.getOrganisationTypeId());
    }

    @Override
    public OrganizationLevel update(OrganizationLevel organizationLevel) {
        OrganizationLevel update = this.findById(organizationLevel.getId());
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, organizationLevel);
        } catch (Exception e) {
            throw new RuntimeException("Id doesn't Exists");
        }
        return officeLevelRepo.save(update);
    }
}
