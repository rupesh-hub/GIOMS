package com.gerp.attendance.repo;

import com.gerp.attendance.model.postAttendance.PostAttendanceRequestDetail;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;


public interface PostAttendanceRequestDetailRepo extends GenericSoftDeleteRepository<PostAttendanceRequestDetail, Long> {
}
