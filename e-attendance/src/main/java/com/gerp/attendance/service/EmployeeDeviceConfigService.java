package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.EmployeeDeviceConfigPojo;
import com.gerp.attendance.model.device.EmployeeDeviceConfig;
import com.gerp.shared.generic.api.GenericService;

public interface EmployeeDeviceConfigService extends GenericService<EmployeeDeviceConfig, Integer> {
    EmployeeDeviceConfig save(EmployeeDeviceConfigPojo employeeDeviceConfigPojo);
}
