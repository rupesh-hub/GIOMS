package com.gerp.attendance.repo;

import com.gerp.attendance.model.device.EmployeeDeviceConfig;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EmployeeDeviceConfigRepo extends GenericSoftDeleteRepository<EmployeeDeviceConfig, Integer> {
}
