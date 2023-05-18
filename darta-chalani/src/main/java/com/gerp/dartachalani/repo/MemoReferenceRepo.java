package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.MemoReference;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemoReferenceRepo extends GenericSoftDeleteRepository<MemoReference, Long> {

    @Query(value = "select chalani_reference_id from memo_reference where memo_id = ?1 and is_active = true", nativeQuery = true)
    List<Long> getDispatchReferences(Long memoId);

    @Query(value = "select darta_reference_id from memo_reference where memo_id = ?1 and is_active = true", nativeQuery = true)
    List<Long> getMemoDartaReference(Long memoId);

    @Query(value = "select referenced_memo_id from memo_reference where dispatch_id = ?1 and is_active = true", nativeQuery = true)
    List<Long> getChalaniMemoReference(Long dispatchId);

    @Query(value = "select darta_reference_id from memo_reference where dispatch_id = ?1 and is_active = true", nativeQuery = true)
    List<Long> getChalaniDartaReference(Long dispatchId);

    @Query(value = "select chalani_reference_id from memo_reference where dispatch_id = ?1 and is_active = true", nativeQuery = true)
    List<Long> getChalaniChalaniReference(Long dispatchId);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where memo_id = ?1 and referenced_memo_id = ?2 and pis_code = ?3 ", nativeQuery = true)
    void deleteReference(Long memoId, Long referenceId, String pisCode);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where memo_id = ?1 and referenced_memo_id = ?2 and pis_code = ?3 and is_attach = true", nativeQuery = true)
    void deleteMemoAttach(Long memoId, Long referenceId, String pisCode);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where memo_id = ?1 and chalani_reference_id = ?2 and pis_code = ?3  and is_attach = true", nativeQuery = true)
    void deleteDispatchAttach(Long memoId, Long referenceId, String pisCode);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where memo_id = ?1 and darta_reference_id = ?2 and pis_code = ?3 and is_attach = true", nativeQuery = true)
    void deleteDartaAttach(Long memoId, Long referenceId, String pisCode);

    @Modifying
    @Query(value = "update memo_reference set include = false where dispatch_id = ?1 and chalani_reference_id = ?2", nativeQuery = true)
    void chalaniInclude(Long id, Long chalaniId);

    @Modifying
    @Query(value = "update memo_reference set include = false where dispatch_id = ?1 and referenced_memo_id = ?2", nativeQuery = true)
    void tippaniInclude(Long id, Long tippaniId);

    @Modifying
    @Query(value = "update memo_reference set include = false where dispatch_id = ?1 and darta_reference_id = ?2", nativeQuery = true)
    void dartaInclude(Long id, Long dartaId);

    @Modifying
    @Query(value = "update memo_reference set is_editable = false where memo_id = ?1 and pis_code = ?2", nativeQuery = true)
    void updateEditableByMemoId(Long id, String pisCode);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where id = ?1", nativeQuery = true)
    void softDeleteReference(Long id);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where referenced_memo_id = ?1 and memo_id = ?2 and is_attach = true", nativeQuery = true)
    void softDeleteReferenceMemo(Long id, Long memoId);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where  chalani_reference_id = ?1 and memo_id = ?2 and is_attach = true", nativeQuery = true)
    void softDeleteReferenceDispatch( Long id, Long memoId);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where darta_reference_id = ?1 and memo_id = ?2 and is_attach = true", nativeQuery = true)
    void softDeleteReferenceDarta(Long id, Long memoId);

    @Modifying
    @Query(value = "update memo_reference set is_active = false where memo_id = ?1 and is_attach = false", nativeQuery = true)
    void deleteMemoReference(Long referencedMemoId);

}
