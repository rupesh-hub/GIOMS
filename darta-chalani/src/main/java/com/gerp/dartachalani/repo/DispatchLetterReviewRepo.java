package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.dispatch.DispatchLetterReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface DispatchLetterReviewRepo extends JpaRepository<DispatchLetterReview,Long> {

    @Query(value = "select * from dispatch_letter_review where receiver_pis_code in ?1 and dispatch_id=?2 and is_active = true order by created_date desc limit 1", nativeQuery = true)
    DispatchLetterReview getDispatchLetterByPisCodeAndDispatchLetter(Set<String> pisCodes, Long dispatchLetter);

    @Query(value = "select * from dispatch_letter_review where is_active= true and dispatch_id=?1 order by created_date desc limit 1", nativeQuery = true)
    DispatchLetterReview getDispatchByActiveStatus(Long dispatchId);

    @Modifying
    @Query(value = "update dispatch_letter_review set is_seen = true where dispatch_id = ?1 and receiver_pis_code = ?2 and is_active = true", nativeQuery = true)
    void setSeen(Long dispatchid, String receiverPisCode);

    @Modifying
    @Query(value = "update dispatch_letter_review set content_log_id = ?1 where dispatch_id = ?2 and content_log_id = NULL", nativeQuery = true)
    void setReviewContentId(Long contentId, Long dispatchId);

    @Query(value = "select * from dispatch_letter_review where sender_pis_code = ?1 and dispatch_id = ?2 order by id desc limit 1", nativeQuery = true)
    DispatchLetterReview findLatestByUser(String pisCode, Long dispatchId);

    @Query(value = "select * from dispatch_letter_review where receiver_pis_code= ?1 and dispatch_id=?2 and receiver_section_code = ?3", nativeQuery = true)
    List<DispatchLetterReview> getAllDispatchLetterReviewByPisCodeAndSectionCode(String pisCode, Long dispatchLetter, String sectionCode);

    @Query(value = "select distinct receiver_pis_code  from dispatch_letter_review dlr where dlr.dispatch_id = ?1 and (reverted is null or reverted = false) and receiver_pis_code  not in (?2)", nativeQuery = true)
    List<String> getMiddleReviewerPisCode(Long dispatchLetterId, String approverPisCode);

    @Modifying
    @Query(value = "update dispatch_letter_review set is_active = false where id = ?1 ", nativeQuery = true)
    void setActive(Long dispatchLetterReviewId);
}
