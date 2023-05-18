package com.gerp.attendance.repo;

import com.gerp.attendance.model.dailyLog.DailyLog;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DailyLogRepo extends GenericSoftDeleteRepository<DailyLog, Long> {

    @Modifying
    @Query(value = "UPDATE daily_log SET is_active = NOT is_active WHERE id = ?1", nativeQuery = true)
    void softDelete(Long id);


    @Transactional
    @Modifying
    @Query(value = "update daily_log set status = ?1 where id = ?2 ", nativeQuery = true)
    void updateStatus(String status, Long id);
}
