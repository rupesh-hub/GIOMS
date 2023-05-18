package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReceivedLetterForwardRepo extends GenericSoftDeleteRepository<ReceivedLetterForward, Long> {

    @Modifying
    @Query(value = "update received_letter_forward set is_seen = true where received_letter_id = ?1 and receiver_pis_code = ?2 and is_active = true", nativeQuery = true)
    void setSeen(Long receivedLetterId, String receiverPisCode);

    @Query(value = "select * from received_letter_forward where received_letter_id = ?1 and receiver_pis_code= ?2 and is_active =true and is_cc = false and receiver_section_id = ?3",nativeQuery = true)
    ReceivedLetterForward findByReceivedLetterIdAndReceiverPisCode(Long receivedLetterId, String pisCode, String sectionCode);

    @Query(value = "select receiver_pis_code from received_letter_forward where received_letter_id = ?1 and is_reverted =false order by id asc ",nativeQuery = true)
    List<String> getPisCodeList(Long receivedLetterId);

    @Query(value="select * from received_letter_forward where receiver_pis_code = ?1 and (is_reverted is null or is_reverted = false) and received_letter_id =?2 order by id desc Limit 1",nativeQuery = true)
    ReceivedLetterForward findBySenderParentCode(String senderParentCode, Long receivedLetterId);

    @Query(value = "select * from received_letter_forward where received_letter_id = ?1 and receiver_pis_code = ?2 and receiver_section_id = ?3", nativeQuery = true)
    List<ReceivedLetterForward> findReceivedLetterByPisAndSection(Long receivedLetterId, String receiverPisCode, String receiverSectionId);

    @Query(value = "select case when count(rlf.id) > 0 then false else true end from received_letter_forward rlf where rlf.received_letter_id  = ?1 and rlf.is_active = true and rlf.completion_status != 'FN'", nativeQuery = true)
    Boolean isAllLetterFinalized(Long receivedLetterId);

    @Query(value = "select * from received_letter_forward where received_letter_id = ?1 " +
            "order by created_date desc limit 1", nativeQuery = true)
    ReceivedLetterForward getLatestForward(Long id);
}
