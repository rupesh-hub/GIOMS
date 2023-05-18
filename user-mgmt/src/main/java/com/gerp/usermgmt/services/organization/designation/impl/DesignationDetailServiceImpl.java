package com.gerp.usermgmt.services.organization.designation.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.deisgnation.DesignationDetail;
import com.gerp.usermgmt.repo.designation.DesignationDetailRepo;
import com.gerp.usermgmt.services.organization.designation.DesignationDetailService;

public class DesignationDetailServiceImpl extends GenericServiceImpl<DesignationDetail, Long> implements DesignationDetailService {

    private final DesignationDetailRepo designationDetailRepo;

    public DesignationDetailServiceImpl(DesignationDetailRepo designationDetailRepo) {
        super(designationDetailRepo);
        this.designationDetailRepo = designationDetailRepo;
    }
}
