package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RequestedDaysRepo extends GenericSoftDeleteRepository<LeaveRequestDetail, Long> {

    @Modifying
    @Query(value = "delete  from requested_days rds where rds.leave_request_id=?1", nativeQuery = true)
    void deleteByLeaveRequest(long id);
}
