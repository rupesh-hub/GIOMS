package com.gerp.usermgmt.services.organization.office;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.office.OrganisationType;

import java.util.List;

public interface OfficeTypeService extends GenericService<OrganisationType, Long> {

    List<OrganisationType> getAll();

    List<IdNamePojo> findAll();

    void deActiveOfficeType(Long id);
}
