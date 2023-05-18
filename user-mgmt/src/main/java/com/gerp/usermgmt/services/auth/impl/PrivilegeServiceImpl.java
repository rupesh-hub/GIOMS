package com.gerp.usermgmt.services.auth.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.Privilege;
import com.gerp.usermgmt.repo.auth.PrivilegeRepo;
import com.gerp.usermgmt.services.auth.PrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrivilegeServiceImpl extends GenericServiceImpl<Privilege, Long> implements PrivilegeService {

    private final PrivilegeRepo privilegeRepo;
    private final CustomMessageSource customMessageSource;

    public PrivilegeServiceImpl(PrivilegeRepo privilegeRepo, CustomMessageSource customMessageSource) {
        super(privilegeRepo);
        this.privilegeRepo = privilegeRepo;
        this.customMessageSource = customMessageSource;
    }
}
