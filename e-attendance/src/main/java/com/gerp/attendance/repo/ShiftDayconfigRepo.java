package com.gerp.attendance.repo;

import com.gerp.attendance.model.shift.ShiftDayConfig;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShiftDayconfigRepo extends GenericSoftDeleteRepository<ShiftDayConfig, Integer> {

    @Query(value = "select * from shift_day_config sdc where sdc.is_active =true and sdc.day_id=?1", nativeQuery = true)
    Optional<ShiftDayConfig> getByDayId(Integer id);
}
