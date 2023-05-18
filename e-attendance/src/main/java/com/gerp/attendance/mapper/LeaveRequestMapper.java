package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.shared.enums.AttendanceStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface LeaveRequestMapper {

    ArrayList<LeaveRequestLatestPojo> getAllLeaveRequest();

    LeaveRequestLatestPojo getLeaveRequestById(Long id);

    LeaveRequestMainPojo leaveRequestBulk(Long id);

    LeaveResponsePojo leaveRequestByDetailId(@Param("detailId") final Long detailId);

    List<DocumentPojo> selectDocument(@Param("id") final Long id);

    LeaveRequestLatestPojo getLeaveRequestByIdDetail(Long id);

    ArrayList<LeaveRequestLatestPojo> getLeaveRequestByEmpPisCode(@Param("pisCode") String pisCode, @Param("dateEn") LocalDate dateEn);

    Long getPreviousLeave(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("fromDateEn") LocalDate fromDateEn, @Param("toDateEn") LocalDate toDateEn,
                          @Param("leaveId") Long leaveId);


    KararEmployeeDetailPojo getKararEmployeeDetail(@Param("pisCode") String pisCode,
                                                   @Param("fromDate") LocalDate fromDate,
                                                   @Param("toDate") LocalDate toDate);

    Long getAccumulatedLeaveForEmployee(@Param("pisCode") String pisCode, @Param("policyId") Long policyId);

    ArrayList<LeaveRequestLatestPojo> getLeaveRequestByOfficeCode(String officeCode);

    ArrayList<LeaveRequestLatestPojo> getLeaveRequestByApproverPisCode(String pisCode);

    Long getLeaveRequestByEmpPisCodeAndDateRange(@Param("pisCode") String pisCode, @Param("approvalStatus") List<String> approvalStatus,
                                                 @Param("toDateEn") LocalDate toDateEn, @Param("fromDateEn") LocalDate fromDateEn,
                                                 @Param("year") String year);

    Long getAccumulatedLeave(@Param("policyId") Long policyId, @Param("pisCode") String pisCode, @Param("toDateEn") LocalDate toDateEn, @Param("fromDateEn") LocalDate fromDateEn);

    LeaveTakenPojo getTotalLeave(@Param("policyId") Long policyId, @Param("pisCode") String pisCode, @Param("toDateEn") LocalDate toDateEn, @Param("fromDateEn") LocalDate fromDateEn, @Param("days") double days, @Param("additionalLeave") Double additionalLeave, @Param("totalLeaveAccumulatedMonthly") Double totalLeaveAccumulatedMonthly,
                                 @Param("year") String year);

    LeaveTakenPojo getTotalLeaveReverted(@Param("policyId") Long policyId,
                                         @Param("pisCode") String pisCode,
                                         @Param("toDateEn") LocalDate toDateEn,
                                         @Param("fromDateEn") LocalDate fromDateEn,
                                         @Param("days") double days,
                                         @Param("additionalLeave") double additionalLeave);

    LeaveTakenPojo getHomeLeaveReverted(@Param("remainingLeaveId") Long remainingLeaveId,
                                        @Param("days") double days);

    ArrayList<LeaveRequestMinimalPojo> getLeaveByMonthYear(@Param("pisCode") String pisCode, @Param("month") Double month, @Param("year") Double year);

    ArrayList<LeaveRequestMinimalPojo> getLeaveByDateRange(@Param("pisCode") String pisCode, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    Boolean checkAllowedDays(@Param("policyId") Long policyId, @Param("toDateEn") LocalDate toDateEn, @Param("fromDateEn") LocalDate fromDateEn);

    Long saveLeave(@Param("policyId") Long policyId, @Param("pisCode") String pisCode, @Param("toDateEn") LocalDate toDateEn, @Param("fromDateEn") LocalDate fromDateEn);

    Boolean checkForRepetitionFy(@Param("policyId") Long policyId, @Param("approvalStatus") List<String> approvalStatus, @Param("empCode") String empCode, @Param("fiscalYear") Integer fiscalYear);

    Boolean checkForRepetition(@Param("policyId") Long policyId, @Param("approvalStatus") List<String> approvalStatus, @Param("empCode") String empCode);

    Boolean checkForTotalAllowed(@Param("policyId") Long policyId, @Param("toDateEn") LocalDate toDateEn, @Param("fromDateEn") LocalDate fromDateEn, @Param("pisCode") String pisCode);

    Page<LeaveReportDataPojo> filterData(Page<LeaveReportDataPojo> page,
                                         @Param("year") String year,
                                         @Param("forReport") Boolean forReport,
                                         @Param("isApprover") Boolean isApprover,
                                         @Param("pisCode") String pisCode,
                                         @Param("approverPisCode") String approverPisCode,
                                         @Param("officeCode") String officeCode,
                                         @Param("searchField") Map<String, Object> searchField);

    LatestApprovalActivityPojo detailOnCancelLeave(@Param("leaveId") Long leaveId);

    Page<LeaveReportDataPojo> filterDataPaginatedLeave(Page<LeaveReportDataPojo> page,
                                                       @Param("officeCode") String officeCode,
                                                       @Param("year") String year,
                                                       @Param("forReport") String forReport,
                                                       @Param("pisCode") String pisCode,
                                                       @Param("approverPisCode") String approverPisCode,
                                                       @Param("userStatus") Boolean userStatus,
                                                       @Param("isApprover") Boolean isApprover,
                                                       @Param("searchField") Map<String, Object> searchField);

    Page<LeaveHistoryPojo> getLeaveHistoryByPisCode(final Page<LeaveHistoryPojo> page, @Param("pisCode") final String pisCode);

    Page<LeaveRequestMainPojo> paginatedLeave(Page<LeaveRequestMainPojo> page,
                                              @Param("officeCode") String officeCode,
                                              @Param("year") String year,
                                              @Param("forReport") String forReport,
                                              @Param("pisCode") String pisCode,
                                              @Param("approverPisCode") String approverPisCode,
                                              @Param("userStatus") Boolean userStatus,
                                              @Param("isApprover") Boolean isApprover,
                                              @Param("searchField") Map<String, Object> searchField);

    Page<LeaveRequestMainPojo> employeePaginatedLeave(Page<LeaveRequestMainPojo> page,
                                                      @Param("officeCode") String officeCode,
                                                      @Param("year") String year,
                                                      @Param("forReport") String forReport,
                                                      @Param("pisCode") String pisCode,
                                                      @Param("approverPisCode") String approverPisCode,
                                                      @Param("userStatus") Boolean userStatus,
                                                      @Param("isApprover") Boolean isApprover,
                                                      @Param("searchField") Map<String, Object> searchField,
                                                      @Param("isManualLeave") Boolean isManualLeave
                                                      );

    Page<LeaveReportDataPojo> filterForLeaveData(Page<LeaveReportDataPojo> page,
                                                 @Param("year") String year,
                                                 @Param("forReport") Boolean forReport,
                                                 @Param("isApprover") Boolean isApprover,
                                                 @Param("pisCode") String pisCode,
                                                 @Param("approverPisCode") String approverPisCode,
                                                 @Param("officeCode") String officeCode,
                                                 @Param("userStatus") Boolean userStatus,
                                                 @Param("searchField") Map<String, Object> searchField);

    List<LeaveReportDataPojo> filterDataForExcel(
            @Param("fiscalYear") Long fiscalYear,
            @Param("year") String year,
            @Param("pisCode") String pisCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("approvalStatus") String approvalStatus);

    List<LeaveOnSameMonthPojo> getLeaveSameMonth(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    List<DifferentMonthPojo> getLeaveDifferentMonth(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    @Select("")
    Long getDiffMonth(@Param("fromDate") LocalDate fromDate,
                      @Param("toDate") LocalDate toDate);

    @Select("select (date_trunc('month', #{fromDate}::date)+ interval '1 month'- interval '1 day')::date")
    LocalDate getLastDate(@Param("fromDate") LocalDate fromDate);

    @Select("SELECT DATE_PART('day', #{toDate}::timestamp - #{fromDate}::timestamp)")
    Long getdays(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Select("SELECT EXTRACT(MONTH FROM DATE (#{fromDate}))")
    Long getMonth(@Param("fromDate") LocalDate fromDate);

    @Select("select (date_trunc('month', #{fromDate}::date)+ interval '1 month')::date")
    LocalDate getFirsDayOfMonth(@Param("fromDate") LocalDate fromDate);

    List<EmployeeOnLeavePojo> getEmployeeOnLeave(@Param("currentDate") LocalDate currentDate, @Param("officeCode") String officeCode);


    List<EmployeeAbsentPojo> getEmployeeOnAbsent(@Param("currentDate") LocalDate currentDate, @Param("officeCode") String officeCode, @Param("attendanceStatus") List<AttendanceStatus> attendanceStatus);


    List<String> getLeaveDatesOnMonth(@Param("officeCode") String officeCode, @Param("pisCode") String pisCode, @Param("month") Double month);

    Long getPreviousLeaveTaken(@Param("pisCode") String pisCode,
                               @Param("leaveDetailId") Long leaveDetailId,
                               @Param("policyId") Long policyId,
                               @Param("fiscalYear") Long fiscalYear,
                               @Param("officeCode") String officeCode);

    Double getMonthlyLeaveTaken(
            @Param("pisCode") String pisCode,
            @Param("leavePolicyId") Long leavePolicyId,
            @Param("year") String year,
            @Param("officeCode") String officeCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    Integer getRepetitionLeave(
            @Param("pisCode") String pisCode,
            @Param("policyId") Long policyId,
            @Param("year") String year,
            @Param("officeCode") String officeCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    Double getTotalLeaveTaken(@Param("pisCode") String pisCode,
                              @Param("fiscalYear") Long fiscalYear,
                              @Param("officeCode") String officeCode);

    DatesPojo fiscalYearDate();

    String getNepaliYear(@Param("currentDate") Date currentDate);


    Boolean checkForPendingLeave(@Param("pisCode") String pisCode);

    Boolean checkForApproveLeave(@Param("pisCode") String pisCode);

    Double currentYearLeave(@Param("pisCode") String pisCode, @Param("year") String year);

    Double getAccumulatedLeaveUpdate(@Param("toDate") LocalDate toDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("pisCode") String pisCode,
                                     @Param("leaveCount") Long leaveCount,
                                     @Param("allowedDays") Integer allowedDays);

    Boolean validatePreviousYear(@Param("pisCode") String pisCode,
                                 @Param("leavePolicyId") Long leavePolicyId,
                                 @Param("officeCode") String officeCode,
                                 @Param("fromDateEn") LocalDate fromDateEn,
                                 @Param("toDateEn") LocalDate toDateEn,
                                 @Param("days") Double days,
                                 @Param("previousYear") String previousYear);

    Boolean previousLeaveCheck(@Param("pisCode") String pisCode,
                               @Param("leavePolicyId") Long leavePolicyId,
                               @Param("previousYear") String previousYear,
                               @Param("days") Double days,
                               @Param("fromDateEn") LocalDate fromDateEn,
                               @Param("toDateEn") LocalDate toDateEn);


    void updatePreviousAccumlatedLeave(@Param("pisCode") String pisCode,
                                       @Param("leavePolicyId") Long leavePolicyId,
                                       @Param("previousYear") String previousYear,
                                       @Param("days") Double days,
                                       @Param("fromDateEn") LocalDate fromDateEn,
                                       @Param("toDateEn") LocalDate toDateEn);

    Double getNewAccumulatedLeave(@Param("pisCode") String pisCode,
                                  @Param("leavePolicyId") Long leavePolicyId,
                                  @Param("previousYear") String previousYear,
                                  @Param("fromDateEn") LocalDate fromDateEn,
                                  @Param("toDateEn") LocalDate toDateEn,
                                  @Param("totalDays") Integer totalDays);


    void updateCurrentYearAccumulated(@Param("pisCode") String pisCode,
                                      @Param("leavePolicyId") Long leavePolicyId,
                                      @Param("year") String year,
                                      @Param("days") Double days,
                                      @Param("fromDateEn") LocalDate fromDateEn,
                                      @Param("toDateEn") LocalDate toDateEn);


    Double getPreviousKararLeave(@Param("pisCode") String pisCode,
                                 @Param("fromDateEn") LocalDate fromDateEn,
                                 @Param("toDateEn") LocalDate toDateEn,
                                 @Param("leavePolicyId") Long leavePolicyId);

    List<LeaveAppliedOthersPojo> leaveAppliedForOther(Long detailId);

    EmployeeDetailPojo selectLeaveEmpDetail(@Param("pisCode") String pisCode);

    LeaveResponsePojo getReviewerDetail(@Param("detailId") Long detailId,
                                        @Param("pisCode") String pisCode);

    @Select("select count(lrd.id) from leave_request_detail lrd where lrd.id = #{detailId} and lrd.pis_code is not null;")
    Long appliedForOther(Long detailId);

    EmployeeDetailPojo selectPisDetail(@Param("pisCode") String pisCode, @Param("detailId") Long detailId);

    EmployeeDetailPojo selectPisDetail2(@Param("pisCode") String pisCode, @Param("detailId") Long detailId);

    List<LeaveRequestDatePojo> selectRequestDate(Long detailsId);

    @Select("select e.office_code from employee e where e.pis_code = #{pisCode};")
    String getOfficeCode(String pisCode);

    @Select("select lrd.id from leave_request_detail lrd where lrd.leave_request_id = #{leaveId}")
    List<Long> getDetailIds(Long leaveId);

    void discardSelectedLeave(
            @Param("remarks") String remarks,
            @Param("detailId") Long detailId
    );

    Long selectLeaveDetailId(@Param("pisCode") String pisCode,
                             @Param("leaveId") Long leaveId);


    void deleteLeaveDocuments(@Param("ids") final List<Long> ids);
    void updateLeaveRequestDocument(@Param("id") final Long id, @Param("documentIds") final List<Long> documentIds);

}
