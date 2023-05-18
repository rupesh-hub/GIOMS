package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.LeaveRequest;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeaveRequestRepo extends GenericSoftDeleteRepository<LeaveRequest, Long> {

    @Transactional
    @Modifying
    @Query(value = "update leave_request_detail set status = ?1 where id = ?2 ", nativeQuery = true)
    void updateStatus(String status, Long id);

}
