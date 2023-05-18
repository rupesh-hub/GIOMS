package com.gerp.attendance.service.impl;

import com.gerp.attendance.model.attendances.AttendanceType;
import com.gerp.attendance.repo.AttendanceTypeRepo;
import com.gerp.attendance.service.AttendanceTypeService;
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
public class AttendanceTypeServiceImpl extends GenericServiceImpl<AttendanceType, Integer> implements AttendanceTypeService {

    private final AttendanceTypeRepo attendanceTypeRepo;
    private final CustomMessageSource customMessageSource;

    public AttendanceTypeServiceImpl(AttendanceTypeRepo attendanceTypeRepo, CustomMessageSource customMessageSource) {
        super(attendanceTypeRepo);
        this.attendanceTypeRepo = attendanceTypeRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public AttendanceType create(AttendanceType newInstance) {
        return super.create(newInstance);
    }

    @Override
    public AttendanceType findById(Integer uuid) {
        AttendanceType attendanceType = super.findById(uuid);
        if (attendanceType == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("Attendance Type")));
        return attendanceType;
    }
}
