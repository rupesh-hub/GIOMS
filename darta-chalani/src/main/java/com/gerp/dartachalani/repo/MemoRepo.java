package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Timestamp;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface MemoRepo extends GenericSoftDeleteRepository<Memo, Long> {

    @Modifying
    @Query(value = "update memo set is_active = false where id = ?1", nativeQuery = true)
    void softDelete(Long memoId);

    @Modifying
    @Query(value = "update memo set is_archive = true where id = ?1", nativeQuery = true)
    void archive(Long memoId);

    @Modifying
    @Query(value = "update memo set is_archive = false where id = ?1", nativeQuery = true)
    void restore(Long memoId);

    @Transactional
    @Modifying
    @Query(value = "update memo set status = ?1, last_modified_date = ?3 where id = ?2", nativeQuery = true)
    void updateStatus(String status, Long id, Timestamp modifiedDate);

    @Query(value = "select case when status = 'P' and is_external is true then true else false end from memo_approval ma where ma.memo_id = ?1 order by id desc limit 1", nativeQuery = true)
    Boolean getMemoStatus(Long memoId);

    @Query(value = "select case when count(id)>0 then 'true' else 'false' end from memo where id = ?1 and is_draft = true", nativeQuery = true)
    boolean checkIsDraft(Long memoId);
}
