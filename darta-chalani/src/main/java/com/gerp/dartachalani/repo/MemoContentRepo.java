package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.model.memo.MemoContent;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemoContentRepo extends GenericSoftDeleteRepository<MemoContent, Long> {

    @Query(value = "select id from memo_content where is_active = true and editable = true and memo_id = ?1 and pis_code = ?2", nativeQuery = true)
    List<Long> getEditable(Long id, String pisCode);

    @Modifying
    @Query(value = "update memo_content set editable = false where memo_id = ?1 and pis_code = ?2", nativeQuery = true)
    void updateEditableByMemoId(Long id, String pisCode);

    @Modifying
    @Query(value = "update memo_content set editable = false where id = ?1", nativeQuery = true)
    void setEditable(Long cId);

    @Modifying
    @Query(value = "update memo_content set is_active = false where id = ?1", nativeQuery = true)
    void softDelete(Long contentId);

//    @Query("select m from MemoContent m where m.memo=?1 and m.pisCode=?2 order by m.id desc limit 1 ")
    Optional<MemoContent> findFirstByMemoAndPisCodeOrderByIdDesc(Memo memo, String pisCode);

    @Modifying
    @Query(value = "update memo_content set is_external_editable = false where memo_id = ?1", nativeQuery = true)
    void updateExternalEditableByMemoId(Long id);
}
