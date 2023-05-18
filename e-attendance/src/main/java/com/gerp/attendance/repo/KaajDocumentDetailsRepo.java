package com.gerp.attendance.repo;

import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.model.leave.KaajRequestDocumentDetails;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface KaajDocumentDetailsRepo extends GenericSoftDeleteRepository<KaajRequestDocumentDetails, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from kaaj_document_details where document_id=?1 and kaaj_request_id = ?2 ", nativeQuery = true)
    void deleteDoc(Long documentId, Long kaajId);
}