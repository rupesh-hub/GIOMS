package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.dto.SectionInvolvedPojo;
import com.gerp.dartachalani.model.receive.ReceivedLetter;
import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReceivedLetterRepo extends GenericSoftDeleteRepository<ReceivedLetter, Long> {

    @Modifying
    @Query(value = "UPDATE received_letter set is_active = not is_active where id = ?1", nativeQuery = true)
    void softDelete(Long letterId);

    @Modifying
    @Query(value = "UPDATE received_letter_document_details SET is_active = false WHERE document_id = ?1", nativeQuery = true)
    void softDeleteDoc(Long id);

    @Modifying
    @Query(value = "UPDATE received_letter SET  status = ?2 WHERE id = ?1", nativeQuery = true)
    void updateStatus(Long id, String status);


}
