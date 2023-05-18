package com.gerp.attendance.repo;

import com.gerp.attendance.model.shift.ShiftEmployeeConfig;
import com.gerp.attendance.model.shift.ShiftEmployeeGroupConfig;
import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShiftEmployeeConfigRepo extends GenericRepository<ShiftEmployeeConfig, Long> {

    @Query(value = "select * from shift_employee_config esc where esc.is_active=true and esc.pis_code=?1", nativeQuery = true)
    Optional<ShiftEmployeeConfig> findEmployeeByCode(String code);

    @Query(value = "select * from shift_employee_config where shift_id = ?1 and pis_code = ?2 ", nativeQuery = true)
    ShiftEmployeeConfig getByShiftIdAndGroupId(Long shiftId, String pisCode);

    @Modifying
    @Transactional
    @Query(value = "delete from ShiftEmployeeConfig where pisCode = :pisCode")
    void deleteAllByPisCode(@Param("pisCode") String pisCode);
}
