package com.gerp.usermgmt.services.organization.office;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.office.OfficeSetup;
import com.gerp.usermgmt.pojo.organization.office.OfficeSetupPojo;

import java.util.List;

public interface OfficeSetupService extends GenericService<OfficeSetup, Long> {
    OfficeSetupPojo findStepStatus(String step);

    Long updateOfficeSetup(OfficeSetupPojo officeSetupPojo);

    List<OfficeSetupPojo> findAllOfficeStepStatus();

}
