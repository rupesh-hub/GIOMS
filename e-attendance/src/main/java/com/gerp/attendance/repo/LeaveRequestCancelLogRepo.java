package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.attendance.model.leave.LeaveRequestCancelLog;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeaveRequestCancelLogRepo extends GenericSoftDeleteRepository<LeaveRequestCancelLog, Integer> {
}
