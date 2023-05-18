package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.dispatch.DispatchLetterDocumentDetails;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DispatchDocumentDetailsRepo extends GenericSoftDeleteRepository<DispatchLetterDocumentDetails, Integer> {

    @Modifying
    @Query(value = "update dispatch_letter_document_details set include = false where dispatch_letter_id = ?1 and document_id = ?2", nativeQuery = true)
    void documentInclude(Long id, Long documentId);

    @Modifying
    @Query(value = "UPDATE dispatch_letter_document_details SET is_active = false WHERE document_id = ?1", nativeQuery = true)
    void softDeleteDoc(Long id);
}
