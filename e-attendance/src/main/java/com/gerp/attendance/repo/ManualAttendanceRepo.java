package com.gerp.attendance.repo;

import com.gerp.attendance.model.attendances.ManualAttendance;
import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ManualAttendanceRepo extends GenericRepository<ManualAttendance, Long> {


    @Modifying
    @Query(value = "update manual_attendance set status = ?1 where id = ?2", nativeQuery = true)
    void updateStatus(String status, Long id);
}
