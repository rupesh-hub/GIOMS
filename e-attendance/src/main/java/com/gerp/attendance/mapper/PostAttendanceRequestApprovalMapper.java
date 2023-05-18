//package com.gerp.attendance.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.gerp.attendance.model.postAttendance.PostAttendanceRequestApproval;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Select;
//
//@Mapper
//public interface PostAttendanceRequestApprovalMapper extends BaseMapper<PostAttendanceRequestApproval> {
//
//    @Select("select id, approver_pis_code, status, remarks from post_attendance_request_approval where post_attendance_request_detail_id = #{id} and is_active = true ")
//    PostAttendanceRequestApproval findPostAttendanceInActive(Long id);
//}
