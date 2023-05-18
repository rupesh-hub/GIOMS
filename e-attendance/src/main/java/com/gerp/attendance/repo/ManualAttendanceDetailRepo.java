package com.gerp.attendance.repo;

import com.gerp.attendance.model.attendances.ManualAttendanceDetail;
import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ManualAttendanceDetailRepo extends GenericRepository<ManualAttendanceDetail, Long> {

    @Modifying
    @Query(value = "update manual_attendance_detail e set is_active= case when e.is_active=true then false else true end where e.manual_attendance_id = ?1", nativeQuery = true)
    void deleteByManualAttendanceDetail(Long id);

    @Modifying
    @Query(value = "update manual_attendance_detail e set is_active= false, remarks=?2 where pis_code = ?1 and e.manual_attendance_id = ?3", nativeQuery = true)
    void discardInActivePisCode(String discardedPisCode,String remarks, Long id);

    @Query(value = "select count(*) from manual_attendance ma\n" +
            "left join manual_attendance_detail mad on ma.id=mad.manual_attendance_id\n" +
            "where mad.pis_code=?1 and ma.status='P' and ma.date_en=?2 and ma.is_active=true;", nativeQuery = true)
    Long getPiscodeManualDetail(String pisCode, LocalDate dateEn);


    @Modifying
    @Query(value = "update manual_attendance_detail set pis_code = ?1 where id = ?2", nativeQuery = true)
    void updateStatus(String status, Long id);
}
