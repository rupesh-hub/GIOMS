package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.EmployeeIrregularAttendancePojo;
import com.gerp.attendance.model.attendances.EmployeeIrregularAttendance;
import com.gerp.shared.generic.api.GenericService;

public interface EmployeeIrregularAttendanceService extends GenericService<EmployeeIrregularAttendance, Long> {
    EmployeeIrregularAttendance save(EmployeeIrregularAttendancePojo employeeIrregularAttendancePojo);

    EmployeeIrregularAttendancePojo getIrregularEmployeeByPisCode(String pisCode);
}
