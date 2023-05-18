package com.gerp.usermgmt.services.organization.employee;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.employee.EmployeeJoiningDate;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeJoiningDatePojo;

import java.util.List;

public interface EmployeeJoiningDateService extends GenericService<EmployeeJoiningDate, Long> {
    public Long save(EmployeeJoiningDatePojo employeeJoiningDatePojo);
     List<EmployeeJoiningDatePojo> getJoiningDateList(String pisCode);

    void toggleJoiningDate(Long id);
}
