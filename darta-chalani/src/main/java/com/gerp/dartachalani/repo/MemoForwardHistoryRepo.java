package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.MemoForwardHistory;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface MemoForwardHistoryRepo extends GenericSoftDeleteRepository<MemoForwardHistory, Long> {

    @Modifying
    @Query(value = "update memo_forward_history set is_active = false where pis_code = ?1 and memo_id = ?2 and section_code = ?3 and is_active = true", nativeQuery = true)
    void setIsActiveByPisCode(String pisCode, Long memoId, String sectionCode);

    @Query(value = "select * from memo_forward_history where  memo_id = ?1 and is_active = true order by id", nativeQuery = true)
    ArrayList<MemoForwardHistory> getAllInvolvedUsers(Long id);

    @Query(value = "select case when count(*)>0 then true else false end from memo_forward_history where  memo_id = ?1 and is_active = true and pis_code = ?2 and section_code = ?3", nativeQuery = true)
    Boolean checkInvolvedUser(Long id, String pisCode, String sectionCode);

    @Modifying
    @Query(value = "update memo_forward_history set pis_code =?4, section_code = ?5, designation_code =  ?6 where memo_id = ?1 and pis_code = ?2 and section_code = ?3", nativeQuery = true)
    void updateHistory(Long memoId, String pisCode, String sectionCode, String changePis, String changeSection, String changeDesignation);
}
