package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.ScreenGroup;
import com.gerp.usermgmt.repo.ScreenGroupRepo;
import com.gerp.usermgmt.services.usermgmt.ScreenGroupService;
import org.springframework.stereotype.Service;

@Service
public class ScreenGroupServiceImpl extends GenericServiceImpl<ScreenGroup, Long> implements ScreenGroupService {
    private final ScreenGroupRepo screenGroupRepo;

    public ScreenGroupServiceImpl(ScreenGroupRepo screenGroupRepo) {
        super(screenGroupRepo);
        this.screenGroupRepo = screenGroupRepo;
    }
}
