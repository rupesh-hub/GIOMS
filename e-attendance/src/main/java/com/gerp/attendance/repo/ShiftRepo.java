package com.gerp.attendance.repo;

import com.gerp.attendance.model.shift.Shift;
import com.gerp.shared.generic.api.GenericRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShiftRepo extends GenericRepository<Shift, Integer> {


    @Modifying
    @Transactional
    @Query(value = "update shift set is_active = ?2 where id = ?1", nativeQuery = true)
    void updateStatus(Long id, boolean b);
}
