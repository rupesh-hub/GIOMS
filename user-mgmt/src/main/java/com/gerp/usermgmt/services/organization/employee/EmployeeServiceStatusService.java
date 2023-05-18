package com.gerp.usermgmt.services.organization.employee;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.EmployeeServiceStatus;

import java.util.List;

public interface EmployeeServiceStatusService extends GenericService<EmployeeServiceStatus, String> {
List<IdNamePojo> findAll();
}
