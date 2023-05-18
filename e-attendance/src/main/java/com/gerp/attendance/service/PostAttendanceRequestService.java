package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.PostAttendanceRequestPojo;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequest;
import com.gerp.attendance.Pojo.PostAttendanceGetPojo;
import com.gerp.attendance.Pojo.PostAttendanceUpdatePojo;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequestDetail;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.ApprovalPojo;

import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PostAttendanceRequestService extends GenericService<PostAttendanceRequest, Long> {
    PostAttendanceRequest savePostAttendance(PostAttendanceRequestPojo postAttendanceRequestPojo);

    PostAttendanceRequestDetail update(PostAttendanceUpdatePojo postAttendanceUpdatePojo);

    void updateStatus(ApprovalPojo data);

     PostAttendanceGetPojo getPostAttendanceById(Long id);

     ArrayList<PostAttendanceGetPojo> getAllPostAttendance();

    ArrayList<PostAttendanceGetPojo> getPostAttendanceByApprover();

     void deletePostAttendanceById(Long id);

//    PostAttendanceRequestgetPostAttendanceById
}
