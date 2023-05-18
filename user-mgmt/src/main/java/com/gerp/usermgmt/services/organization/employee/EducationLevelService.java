package com.gerp.usermgmt.services.organization.employee;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.EducationLevel;
import com.gerp.usermgmt.model.employee.Religion;

import java.util.List;

public interface EducationLevelService  extends GenericService<EducationLevel, String> {

    List<IdNamePojo> getAllEducationLevelMinimalDetail();
}
