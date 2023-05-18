package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.MemoApproval;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemoApprovalRepo extends GenericSoftDeleteRepository<MemoApproval, Long> {

    @Modifying
    @Query(value = "update memo_approval set is_seen = true where memo_id = ?1 and approver_pis_code = ?2 and is_active = true", nativeQuery = true)
    void setSeen(Long memoId, String receiverPisCode);

    @Query(value = "select * from memo_approval where memo_id = ?1 and approver_pis_code = ?2 and approver_section_code = ?3", nativeQuery = true)
    List<MemoApproval> getAllMemoApprovalsByPisAndSectionCode(Long id, String pisCode, String sectionId);

    @Query(value = "select distinct approver_pis_code  from memo_approval ma where ma.memo_id = ?1 and (ma.reverted is null or ma.reverted = false) and approver_pis_code  not in (?2)", nativeQuery = true)
    List<String> getMiddleApproverPisCode(Long memoId, String finalApproverPisCode);
}
