package com.gerp.attendance.repo;

import com.gerp.attendance.Pojo.DatesPojo;
import com.gerp.attendance.Pojo.MasterDashboardPojo;
import com.gerp.attendance.model.attendances.AttendanceStatus;
import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EmployeeAttendanceRepo extends GenericSoftDeleteRepository<EmployeeAttendance, Long> {

    @Query(value = "select * from employee_attendance where pis_code = ?2 and date_en = ?1 limit 1", nativeQuery = true)
    EmployeeAttendance getByDateAndPisCode(LocalDate x, String pisCode);

    @Query(value = "select *\n" +
            "from employee_attendance\n" +
            "where pis_code = ?1\n" +
            "  and date_en = ?2\n" +
            "order by shift_checkout", nativeQuery = true)
    List<EmployeeAttendance> getEmployeeData(String pisCode, LocalDate deviceDate);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from employee_attendance ea  where ea.pis_code=?1\n" +
            "                                     and ea.date_en between ?2 and ?3\n" +
            "                                     and ea.attendance_status=?4")
    void updateCancel(String pisCode, LocalDate fromDate, LocalDate toDate, String attendanceStatus);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from employee_attendance ea  where ea.pis_code=?1\n" +
            "                                     and ea.date_en between ?2 and ?3")
    void cancelBaatoLeave(String pisCode, LocalDate fromDate, LocalDate toDate);



//    @Modifying
//    @Transactional
//    @Query(nativeQuery = true, value = "delete from employee_attendance ea where ea.manual_attendance_id =?1")
//    void deleteManualAttendance(Long id);
}
