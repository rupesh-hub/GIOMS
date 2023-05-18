package com.gerp.usermgmt.services.organization.administration.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.administrative.AdministrationLevel;
import com.gerp.usermgmt.repo.AdministrationLevelRepo;
import com.gerp.usermgmt.services.organization.administration.AdministrationLevelService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdministrationLevelServiceImpl extends GenericServiceImpl<AdministrationLevel, Integer> implements AdministrationLevelService {

    private final AdministrationLevelRepo administrationLevelRepo;

    public AdministrationLevelServiceImpl(AdministrationLevelRepo administrationLevelRepo) {
        super(administrationLevelRepo);
        this.administrationLevelRepo = administrationLevelRepo;
    }
}
