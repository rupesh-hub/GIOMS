package com.gerp.attendance.repo;

import com.gerp.attendance.model.shift.ShiftEmployeeGroupConfig;
import com.gerp.shared.generic.api.GenericRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShiftEmployeeGroupConfigRepo extends GenericRepository<ShiftEmployeeGroupConfig, Long> {

    @Query(value = "select * from shift_employee_group_config where shift_id = ?1 and shift_employee_group_id = ?2 ", nativeQuery = true)
    ShiftEmployeeGroupConfig getByShiftIdAndGroupId(Long shiftId, Long groupId);


    @Modifying
    @Transactional
    @Query(value = "delete from shift_employee_group_config WHERE shift_employee_group_id = ?1", nativeQuery = true)
    void deleteByShiftGroup(Long shiftGroupId);


}
