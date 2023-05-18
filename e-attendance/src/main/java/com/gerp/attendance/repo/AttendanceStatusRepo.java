package com.gerp.attendance.repo;

import com.gerp.attendance.model.attendances.AttendanceStatus;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

public interface AttendanceStatusRepo extends GenericSoftDeleteRepository<AttendanceStatus, Integer> {
}
