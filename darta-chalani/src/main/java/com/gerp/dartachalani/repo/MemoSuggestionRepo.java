package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.MemoApproval;
import com.gerp.dartachalani.model.memo.MemoSuggestion;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MemoSuggestionRepo extends GenericSoftDeleteRepository<MemoSuggestion, Long> {

//    @Modifying
//    @Query(value = "update memo_suggestion set initial = false where memo_id = ?2 and approver_pis_code = ?1 and initial = true", nativeQuery = true)
//    void setInitial(String initial, Long id);

    @Modifying
    @Query(value = "update memo_suggestion set is_seen = true where memo_id = ?1 and approver_pis_code = ?2 and is_active = true", nativeQuery = true)
    void setSeen(Long memoId, String receiverPisCode);

    @Query(value = "select * from memo_suggestion where memo_id = ?1 and approver_pis_code = ?2 and approver_section_code = ?3", nativeQuery = true)
    List<MemoSuggestion> getAllMemoSuggestionsByPisAndSectionCode(Long id, String pisCode, String sectionId);

    @Query(value = "select ms.* " +
            "FROM memo_suggestion ms " +
            "WHERE ms.sender_pis_code in ?1 and (ms.is_revert = false or ms.is_revert is null) and ms.memo_id = ?2 " +
            "order by ms.id desc limit 1", nativeQuery = true)
    MemoSuggestion getByFirstSender(Set<String> firstSender, Long memoId);

    @Query(value = "select ms.first_sender, ms.initial_sender " +
            "FROM memo_suggestion ms " +
            "WHERE ms.approver_pis_code in ?1 and (ms.is_revert = false or ms.is_revert is null) and ms.memo_id = ?2 " +
            "order by ms.id desc limit 1", nativeQuery = true)
    Map<String, Object> getFirstSenderAndInitialSender(Set<String> firstSender, Long memoId);
}
