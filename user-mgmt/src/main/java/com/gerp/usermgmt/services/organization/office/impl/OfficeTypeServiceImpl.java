package com.gerp.usermgmt.services.organization.office.impl;


import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.office.OfficeType;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.repo.office.OfficeTypeRepo;
import com.gerp.usermgmt.services.organization.office.OfficeTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OfficeTypeServiceImpl extends GenericServiceImpl<OrganisationType, Long> implements OfficeTypeService {
    private final OfficeTypeRepo officeTypeRepo;

    public OfficeTypeServiceImpl(OfficeTypeRepo officeTypeRepo) {
        super(officeTypeRepo);
        this.officeTypeRepo = officeTypeRepo;
    }

    @Override
    public List<IdNamePojo> findAll() {
        return officeTypeRepo.findAllMinimal();
    }

    @Override
    public void deActiveOfficeType(Long id) {
         officeTypeRepo.inactiveOfficeType(id);
    }
}
