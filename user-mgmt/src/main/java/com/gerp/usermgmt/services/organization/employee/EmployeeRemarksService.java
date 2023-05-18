package com.gerp.usermgmt.services.organization.employee;

import com.gerp.usermgmt.model.employee.EmployeeRemarks;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeRemarksPojo;

public interface EmployeeRemarksService {

    EmployeeRemarks save(EmployeeRemarks employeeRemarks);
    EmployeeRemarks update(EmployeeRemarksPojo employeeRemarks);

    EmployeeRemarks getByPisCode(String pisCode);
}
