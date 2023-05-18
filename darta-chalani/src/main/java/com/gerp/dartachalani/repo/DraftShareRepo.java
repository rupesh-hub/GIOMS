package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.dartachalani.model.draft.share.DraftShare;
import com.gerp.shared.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;
import java.util.Optional;

public interface DraftShareRepo extends JpaRepository<DraftShare, Long> {

    Optional<DraftShare> getByReceiverPisCodeAndReceiverSectionCodeAndStatusAndDispatchIdAndIsActive(String receiverPisCode, String receiverSectionCode, Status status, Long dispatchId, Boolean isActive);

    Optional<DraftShare> getByReceiverPisCodeAndReceiverSectionCodeAndDispatchIdAndIsActiveAndLetterType(String receiverPisCode, String receiverSectionCode, Long dispatchId, Boolean isActive, DcTablesEnum letterType);

    Optional<DraftShare> getByReceiverPisCodeAndReceiverSectionCodeAndStatusAndMemoIdAndIsActive(String receiverPisCode, String receiverSectionCode, Status status, Long memoId, Boolean isActive);

    Optional<DraftShare> getByReceiverPisCodeAndReceiverSectionCodeAndMemoIdAndIsActiveAndLetterType(String receiverPisCode, String receiverSectionCode, Long memoId, Boolean isActive, DcTablesEnum letterType);

    boolean existsByReceiverPisCodeAndReceiverSectionCodeAndDispatchIdAndIsActiveAndLetterType(String receiverPisCode, String receiverSectionCode, Long dispatchId, Boolean isActive, DcTablesEnum letterType);

    boolean existsByReceiverPisCodeAndReceiverSectionCodeAndMemoIdAndIsActiveAndLetterType(String receiverPisCode, String receiverSectionCode, Long dispatchId, Boolean isActive, DcTablesEnum letterType);

    @Query(value = "select count(ds.id) from draft_share ds where ds.dispatch_id = ?1 and ds.is_active = true", nativeQuery = true)
    Integer dispatchDraftShareCount(Long dispatchId);

    @Query(value = "select count(ds.id) from draft_share ds where ds.memo_id = ?1 and ds.is_active = true", nativeQuery = true)
    Integer memoDraftShareCount(Long memoId);

    @Query(value = "select case when count(ds.id)>0 then 'true' else 'false' end from draft_share ds where ds.dispatch_id = ?1 and ds.is_active = true and ds.receiver_pis_code = ?2 and ds.receiver_section_code = ?3", nativeQuery = true)
    boolean checkPermissionDispatchShare(Long dispatchId, String receiverPisCode, String receiverSectionCode);

    @Query(value = "select case when count(ds.id)>0 then 'true' else 'false' end from draft_share ds where ds.memo_id = ?1 and ds.is_active = true and ds.receiver_pis_code = ?2 and ds.receiver_section_code = ?3", nativeQuery = true)
    boolean checkPermissionMemoDraft(Long memoId, String receiverPisCode, String receiverSectionCode);

    @Modifying
    @Query(value = "update draft_share set is_active = false where receiver_pis_code = ?1 and receiver_section_code = ?2 and dispatch_id = ?3", nativeQuery = true)
    void removeDispatchDraftShareEmployee(String receiverPisCode, String receiverSectionCode, Long dispatchId);

    @Modifying
    @Query(value = "update draft_share set is_active = false where receiver_pis_code = ?1 and receiver_section_code = ?2 and memo_id = ?3", nativeQuery = true)
    void removeMemoDraftShareEmployee(String receiverPisCode, String receiverSectionCode, Long memoId);

    @Query(value = "select ds.* from draft_share ds where ds.sender_pis_code = ?1 and ds.sender_section_code = ?2 and ds.memo_id = ?3 limit 1", nativeQuery = true)
    DraftShare getSenderMemoDraftShare(String senderPisCode, String senderSectionCode, Long memoId);

    @Query(value = "select ds.* from draft_share ds where ds.sender_pis_code = ?1 and ds.sender_section_code = ?2 and ds.dispatch_id = ?3 limit 1", nativeQuery = true)
    DraftShare getSenderDispatchDraftShare(String senderPisCode, String senderSectionCode, Long dispatchId);

    @Query(value = "select sender_pis_code, sender_section_code from draft_share ds where ds.memo_id = ?1 order by created_date limit 1", nativeQuery = true)
    Map<String, String> getFirstMemoSender(Long memoId);

}
