package com.gerp.attendance.repo;

import com.gerp.attendance.model.setup.PublicHolidaySetup;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PublicHolidaySetupRepo extends GenericSoftDeleteRepository<PublicHolidaySetup, Integer> {
//    @Query(value = "select * from public_holiday_setup phs where phs.is_active =true and phs.name_en=?1", nativeQuery = true)
//    Optional<PublicHolidaySetup> getByName(String name);

    @Modifying
    @Query(value = "UPDATE public_holiday SET is_active = not is_active WHERE id = ?1", nativeQuery = true)
    void softDelete(Integer holidayId);

    @Modifying
    @Query(value = "delete from public_holiday WHERE id = ?1", nativeQuery = true)
    void deletePublicHoliday(Integer holidayId);
}
