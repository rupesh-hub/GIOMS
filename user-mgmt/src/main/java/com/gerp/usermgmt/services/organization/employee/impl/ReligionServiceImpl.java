package com.gerp.usermgmt.services.organization.employee.impl;


import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.employee.Religion;
import com.gerp.usermgmt.repo.employee.ReligionRepo;
import com.gerp.usermgmt.services.organization.employee.ReligionService;
import org.springframework.stereotype.Service;

@Service
public class ReligionServiceImpl extends GenericServiceImpl<Religion, String> implements ReligionService {
    private final ReligionRepo religionRepo;

    public ReligionServiceImpl(ReligionRepo religionRepo) {
        super(religionRepo);
        this.religionRepo = religionRepo;
    }
}
