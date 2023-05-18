package com.gerp.attendance.repo;

import com.gerp.attendance.model.shift.group.ShiftEmployeeGroupMapping;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

public interface ShiftEmployeeGroupMappingRepo extends GenericSoftDeleteRepository<ShiftEmployeeGroupMapping, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from ShiftEmployeeGroupMapping where pisCode = :pisCode")
    void deleteAllBPisCode(@Param("pisCode") String pisCode);

}
