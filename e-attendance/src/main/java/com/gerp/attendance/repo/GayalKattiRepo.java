package com.gerp.attendance.repo;

import com.gerp.attendance.model.gayalKatti.GayalKatti;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface GayalKattiRepo extends GenericSoftDeleteRepository<GayalKatti, Long> {

    @Modifying
    @Query(value = "UPDATE gayal_katti SET is_active = NOT is_active WHERE id = ?1", nativeQuery = true)
    void softDelete(Long holidayId);

    @Transactional
    @Modifying
    @Query(value = "update gayal_katti set status = ?1 where id = ?2 ", nativeQuery = true)
    void updateStatus(String status, Long id);

}
