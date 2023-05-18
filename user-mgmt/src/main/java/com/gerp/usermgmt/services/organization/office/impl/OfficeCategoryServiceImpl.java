package com.gerp.usermgmt.services.organization.office.impl;


import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.office.OfficeCategory;
import com.gerp.usermgmt.repo.office.OfficeCategoryRepo;
import com.gerp.usermgmt.services.organization.office.OfficeCategoryService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OfficeCategoryServiceImpl extends GenericServiceImpl<OfficeCategory, String> implements OfficeCategoryService {
    private final OfficeCategoryRepo officeCategoryRepo;

    public OfficeCategoryServiceImpl(OfficeCategoryRepo officeCategoryRepo) {
        super(officeCategoryRepo);
        this.officeCategoryRepo = officeCategoryRepo;
    }
}
