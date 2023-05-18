package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.MemoDocumentDetails;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemoDocumentDetailsRepo extends GenericSoftDeleteRepository<MemoDocumentDetails, Integer> {

    @Query(value = "SELECT * FROM memo_document_details WHERE document_id = ?1 and memo_id = ?2", nativeQuery = true)
    MemoDocumentDetails getDetail(Long documentId, Long memoId);

    @Modifying
    @Query(value = "UPDATE memo_document_details SET is_active = false WHERE document_id = ?1", nativeQuery = true)
    void softDelete(Long id);

    @Query(value = "select id from memo_document_details where is_active = true and editable = true and memo_id = ?1 and pis_code = ?2", nativeQuery = true)
    List<Long> getEditable(Long id, String pisCode);

    @Modifying
    @Query(value = "update memo_document_details set editable = false where id = ?1", nativeQuery = true)
    void setEditable(Long dId);

    @Modifying
    @Query(value = "update memo_document_details set editable = false where memo_id = ?1 and pis_code = ?2", nativeQuery = true)
    void updateEditableByMemoId(Long dId, String pisCode);
}
