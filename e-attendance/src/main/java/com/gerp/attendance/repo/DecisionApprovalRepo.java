package com.gerp.attendance.repo;

import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DecisionApprovalRepo extends GenericSoftDeleteRepository<DecisionApproval, Long> {

    @Modifying
    @Query(value = "update decision_approval set status = ?1 where id = ?2 ", nativeQuery = true)
    void updateStatus(String status, Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from decision_approval da where da.leave_request_detail_id in  ?1")
    void deleteByLeaveDetailId(List<Long> id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update decision_approval set in_active_status='INACTIVE' where leave_request_detail_id =?1")
    void updateInActiveStatusByLeaveDetailId(Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update decision_approval set in_active_status='INACTIVE' where kaaj_request_id =?1")
    void updateInactiveStatusByKaajId(Long id);

}
