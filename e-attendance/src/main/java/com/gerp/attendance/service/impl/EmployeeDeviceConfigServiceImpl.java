package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.EmployeeDeviceConfigPojo;
import com.gerp.attendance.model.device.EmployeeDeviceConfig;
import com.gerp.attendance.repo.AttendanceDeviceRepo;
import com.gerp.attendance.repo.EmployeeDeviceConfigRepo;
import com.gerp.attendance.service.EmployeeDeviceConfigService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class EmployeeDeviceConfigServiceImpl extends GenericServiceImpl<EmployeeDeviceConfig, Integer> implements EmployeeDeviceConfigService {
    private final EmployeeDeviceConfigRepo employeeDeviceConfigRepo;
    private final CustomMessageSource customMessageSource;
    private final AttendanceDeviceRepo attendanceDeviceRepo;

    public EmployeeDeviceConfigServiceImpl(EmployeeDeviceConfigRepo employeeDeviceConfigRepo, AttendanceDeviceRepo attendanceDeviceRepo, CustomMessageSource customMessageSource) {
        super(employeeDeviceConfigRepo);
        this.employeeDeviceConfigRepo = employeeDeviceConfigRepo;
        this.attendanceDeviceRepo = attendanceDeviceRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public EmployeeDeviceConfig findById(Integer uuid) {
        EmployeeDeviceConfig employeeDeviceConfig = super.findById(uuid);
        if(employeeDeviceConfig == null)
            throw new RuntimeException((customMessageSource.get("error.doesn't.exist", customMessageSource.get("EmployeeAttendanceConfig"))));
        return employeeDeviceConfig;
    }

    @Override
    public EmployeeDeviceConfig save(EmployeeDeviceConfigPojo employeeDeviceConfigPojo) {
        EmployeeDeviceConfig employeeDeviceConfig = new EmployeeDeviceConfig();
        employeeDeviceConfig.setPisCode(employeeDeviceConfigPojo.getPisCode());
        employeeDeviceConfig.setEmpCodeFromDevice(employeeDeviceConfigPojo.getEmpCodeFromDevice());
        employeeDeviceConfig.setAttendanceDevice(employeeDeviceConfigPojo.getAttendanceDeviceId() == null ? null : attendanceDeviceRepo.findById(employeeDeviceConfigPojo.getAttendanceDeviceId()).get());
        employeeDeviceConfigRepo.save(employeeDeviceConfig);
        return employeeDeviceConfig;
    }
}
