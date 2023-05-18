package com.gerp.attendance.repo;

import com.gerp.attendance.model.postAttendance.PostAttendanceRequest;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PostAttendanceRequestRepo extends GenericSoftDeleteRepository<PostAttendanceRequest, Long> {

    @Transactional
    @Modifying
    @Query(value = "update post_attendance_request_detail set status = ?1 where id = ?2 ", nativeQuery = true)
    void updateStatus(String status, Long id);

    @Transactional
    @Modifying
    @Query(value = "update post_attendance_request_detail set status = ?1, is_active = ?2 where id = ?3 ", nativeQuery = true)
    void updateInActive(String status, boolean active, Long id);
}
