package com.gerp.attendance.repo;

import com.gerp.attendance.model.attendances.EmployeeIrregularAttendance;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EmployeeIrregularAttendanceRepo extends GenericSoftDeleteRepository<EmployeeIrregularAttendance, Long> {

}
