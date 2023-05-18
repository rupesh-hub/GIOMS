package com.gerp.usermgmt.services.organization.office;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.office.OrganizationLevel;
import com.gerp.usermgmt.pojo.organization.office.OrganizationLevelPojo;

import java.util.List;

public interface OfficeLevelService extends GenericService<OrganizationLevel, Integer> {
    List<OrganizationLevelPojo> findAll();
}
