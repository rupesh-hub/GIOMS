package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gerp.attendance.Pojo.ActivityLogPojo;
import com.gerp.attendance.Pojo.DailyLogPojo;
import com.gerp.attendance.Pojo.approvalActivity.ApprovalActivityPojo;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.shared.enums.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface DecisionApprovalMapper extends BaseMapper<DecisionApproval> {

    @Select("select id, approver_pis_code, status, remarks from decision_approval where record_id = #{recordId} and code = #{code} and is_active = true and status=#{status}")
    DecisionApproval findActive(@Param("recordId") UUID recordId, @Param("code") String code, @Param("status") Status status);

    @Select("select id, approver_pis_code, status, remarks from decision_approval where record_id = #{recordId} and code = #{code} and manual_attendance_id = #{manualAttendanceId} and is_active = true ")
    DecisionApproval findByManualAttendance(@Param("recordId") UUID recordId, @Param("code") String code, @Param("manualAttendanceId") Long id);

    @Select("select id, approver_pis_code, status, remarks from decision_approval where record_id = #{recordId} and code = #{code}")
    DecisionApproval findAllDecisionApproval(@Param("recordId") UUID recordId, @Param("code") String code);

    @Select("select id, approver_pis_code, status, remarks from decision_approval where record_id = #{recordId} and code = #{code} and post_attendance_id = #{postAttendanceId} and is_active = true ")
    DecisionApproval findByPostAttendance(@Param("recordId") UUID recordId, @Param("code") String code, @Param("postAttendanceId") Long id);

    @Select("SELECT nextval('uniqueapproval_seq')")
    Long getNextVal();

    @Select("select id, approver_pis_code, status, remarks, code from decision_approval where record_id = #{recordId} and code = #{code} and is_active = true ")
    DecisionApproval findActiveDailyLog(@Param("recordId") UUID recordId, @Param("code") String code);

    List<ApprovalActivityPojo> getActivityLogByRecordId(@Param("recordId") UUID recordId, @Param("code") String code);

    List<ApprovalActivityPojo> getActivityLogById(@Param("id") Long id, @Param("code") String code, @Param("limit") int limit);

    @Select("select \n" +
            "\tcase\n" +
            " \t\twhen da.status = 'F' then da.created_date \n" +
            "   \t\t else da.created_date end as forwardedDate from decision_approval da where da.leave_request_detail_id=#{detailId} limit 1")
    String getForwardedDate(@Param("detailId") Long detailId);


    @Select("select status as status," +
            "hash_content as hashContent, " +
            "signature as signature \n" +
            "from decision_approval da \n" +
            "where da.leave_request_detail_id = #{id} " +
            "and status != 'P' " +
            "and (in_active_status !='INACTIVE' or in_active_status is null) " +
            "order by created_date desc")
    List<ApprovalActivityPojo> getLeaveActivity(@Param("id") Long id);


    List<ActivityLogPojo> getActivityLog(@Param("id") final Long id, @Param("code") final String code);

    ActivityLogPojo getLeaveLog(@Param("id") final Long id);

    ActivityLogPojo getKaajLog(@Param("id") final Long id);

    ActivityLogPojo getAttendanceLog(@Param("id") final Long id);

    ActivityLogPojo getDailyLog(@Param("id") final Long id);

    @Select("select concat('LR',lr.emp_pis_code,lrd.from_date_en,lrd.to_date_en,lrd.leave_policy_id,lrd.description) \n" +
            "from leave_request lr \n" +
            "inner join leave_request_detail lrd on lr.id = lrd.leave_request_id\n" +
            "where lrd.id  = #{id}")
    String getLeaveContent(final Long id);

    DailyLogPojo getDailyLogContent(final Long id);

    @Select("select id from leave_request_detail where leave_request_id = " +
            "(select leave_request_id from leave_request_detail where id=#{id}) order by id asc")
    List<Long> getDetailIds(Long id);
}
