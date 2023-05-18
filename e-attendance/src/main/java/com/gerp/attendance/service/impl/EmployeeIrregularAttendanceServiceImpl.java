package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.EmployeeIrregularAttendancePojo;
import com.gerp.attendance.model.attendances.EmployeeIrregularAttendance;
import com.gerp.attendance.model.device.EmployeeDeviceConfig;
import com.gerp.attendance.repo.AttendanceDeviceRepo;
import com.gerp.attendance.repo.EmployeeDeviceConfigRepo;
import com.gerp.attendance.repo.EmployeeIrregularAttendanceRepo;
import com.gerp.attendance.service.EmployeeDeviceConfigService;
import com.gerp.attendance.service.EmployeeIrregularAttendanceService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class EmployeeIrregularAttendanceServiceImpl extends GenericServiceImpl<EmployeeIrregularAttendance, Long> implements EmployeeIrregularAttendanceService {

    private final EmployeeIrregularAttendanceRepo employeeIrregularAttendanceRepo;
    private final CustomMessageSource customMessageSource;

    public EmployeeIrregularAttendanceServiceImpl(EmployeeIrregularAttendanceRepo employeeIrregularAttendanceRepo, CustomMessageSource customMessageSource) {
        super(employeeIrregularAttendanceRepo);
        this.employeeIrregularAttendanceRepo = employeeIrregularAttendanceRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public EmployeeIrregularAttendance save(EmployeeIrregularAttendancePojo employeeIrregularAttendancePojo) {
        return null;
    }

    @Override
    public EmployeeIrregularAttendancePojo getIrregularEmployeeByPisCode(String pisCode) {
        return null;
    }
}
