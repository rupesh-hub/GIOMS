package com.gerp.dartachalani.repo.signature;

import com.gerp.dartachalani.model.signature.SignatureVerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SignatureVerificationLogRepository extends JpaRepository<SignatureVerificationLog, Long> {

    @Query(value = "select svl.is_verified " +
            "from signature_verification_log svl where  " +
            "svl.signature_type = ?1 and memo_id = ?2  " +
            "order by created_date desc limit 1", nativeQuery = true)
    Boolean existsMemoLog(String signatureType, Long memoId);

    @Query(value = "select svl.is_verified " +
            "from signature_verification_log svl where  " +
            "svl.signature_type = ?1 and memo_id = ?2 and svl.memo_content_id = ?3 " +
            "order by created_date desc limit 1", nativeQuery = true)
    Boolean existsMemoContentLog(String signatureType, Long memoId, Long contentId);

    @Query(value = "select svl.is_verified " +
            "from signature_verification_log svl where svl.signature_type = ?1 " +
            "and svl.dispatch_id = ?2 order by created_date desc limit 1", nativeQuery = true)
    Boolean existsDispatchLog(String signatureType, Long dispatchId);

    @Query(value = "select svl.is_verified " +
            "from signature_verification_log svl where svl.signature_type = ?1 " +
            "and svl.dispatch_id = ?2 and svl.dispatch_review_id = ?3 order by created_date desc limit 1", nativeQuery = true)
    Boolean existsDispatchReviewLog(String signatureType, Long dispatchId, Long dispatchReviewId);

}
