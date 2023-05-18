package com.gerp.attendance.repo;

import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.attendance.model.device.AttendanceDevice;
import com.gerp.attendance.model.leave.AccumulatedHomeLeaveLog;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AccumulatedHomeLeaveLogRepo extends GenericSoftDeleteRepository<AccumulatedHomeLeaveLog, Integer> {


    @Query(value = "select * from accumulated_home_leave_log where remaining_leave_id = ?1 and is_active=true limit 1",nativeQuery = true)
    AccumulatedHomeLeaveLog findByRemainingId(Long remainingId);

    @Modifying
    @Query(value = "UPDATE accumulated_home_leave_log set accumulated_leave =  ?1, accumulated_leave_fy= ?2, last_modified_date=current_timestamp where id = ?3 and is_active=true", nativeQuery = true)
    void updateHomeLeave(Double accumulatedLeave,Double accumulatedLeaveFy, Integer id);

    @Modifying
    @Transactional
    @Query(value = "delete from accumulated_home_leave_log ahl where ahl.remaining_leave_id=?1",nativeQuery = true)
    void deleteByRemainingId(Integer id);


}
