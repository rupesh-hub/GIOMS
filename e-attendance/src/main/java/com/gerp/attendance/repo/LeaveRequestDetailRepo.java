package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.attendance.model.leave.LeaveRequest;
import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeaveRequestDetailRepo extends GenericSoftDeleteRepository<LeaveRequestDetail, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from leave_request_detail lrd where lrd.leave_policy_id= ?1")
    void deleteByLeavePolicyId(Long id);

    @Query(nativeQuery = true, value = "select lrd.id from leave_request_detail lrd where lrd.leave_policy_id= ?1")
    List<Long> findByLeavePolicy(Long id);
}
