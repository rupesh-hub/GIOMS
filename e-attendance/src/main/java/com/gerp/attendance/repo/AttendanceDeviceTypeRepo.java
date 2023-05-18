package com.gerp.attendance.repo;

import com.gerp.attendance.model.device.AttendanceDeviceType;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AttendanceDeviceTypeRepo extends GenericSoftDeleteRepository<AttendanceDeviceType, Integer> {
}
