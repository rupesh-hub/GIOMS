package com.gerp.attendance.repo;

import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.model.leave.KaajRequestReferenceDocuments;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface KaajRequestReferenceDocumentsRepo extends GenericSoftDeleteRepository<KaajRequestReferenceDocuments, Integer> {
    @Transactional
    @Modifying
    @Query(value = "delete from kaaj_request_reference_documents where document_id=?1 and kaaj_request_id = ?2 ", nativeQuery = true)
    void deleteDoc(Long documentId, Long kaajId);

}
