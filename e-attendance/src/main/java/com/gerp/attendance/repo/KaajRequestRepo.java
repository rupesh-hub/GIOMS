package com.gerp.attendance.repo;

import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface KaajRequestRepo extends GenericSoftDeleteRepository<KaajRequest, Long> {

    @Transactional
    @Modifying
    @Query(value = "update kaaj_request set status = ?1 where id = ?2 ", nativeQuery = true)
    void updateStatus(String toString, Long recordId);
}
