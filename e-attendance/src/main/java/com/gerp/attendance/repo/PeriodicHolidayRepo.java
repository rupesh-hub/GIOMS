package com.gerp.attendance.repo;

import com.gerp.attendance.model.setup.PeriodicHoliday;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PeriodicHolidayRepo extends GenericSoftDeleteRepository<PeriodicHoliday, Long> {

    @Modifying
    @Query(value = "UPDATE periodic_holiday SET is_active = NOT is_active WHERE id = ?1 ", nativeQuery = true)
    void softDeleteHoliday(Long holidayId);



    @Modifying
    @Query(value = "UPDATE periodic_holiday SET is_specific_holiday = NOT is_specific_holiday WHERE id = ?1 ", nativeQuery = true)
    void toggleSpecificHoliday(Long holidayId);

}
