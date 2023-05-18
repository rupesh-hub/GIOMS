package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceMonthlyReportPojo;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.model.leave.LeaveRequest;
import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.ApprovalPojo;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeaveRequestService extends GenericService<LeaveRequest, Long> {
    LeaveRequest save(LeaveRequestPojo leaveRequestPojo);

    LeaveRequestDetail update(LeaveRequestUpdatePojo leaveRequestPojo);

    LeaveResponsePojo getLeaveById(final Long id);

    Page<LeaveHistoryPojo> getLeaveHistoryByPisCode(final GetRowsRequest paginatedRequest);

    ArrayList<LeaveRequestLatestPojo> getLeaveByPisCode();

    ArrayList<LeaveRequestLatestPojo> getLeaveByOfficeCode();

    ArrayList<LeaveRequestLatestPojo> getAllLeaveRequest();

    void deleteLeaveRequest(Long id);

    void updateStatus(ApprovalPojo data) throws ParseException;

    void updateNewYearLeave(ApprovalPojo data);

    ArrayList<LeaveRequestLatestPojo> getLeaveByApproverPisCode();

    ArrayList<LeaveRequestMinimalPojo> getLeaveByMonthAndYear(String pisCode, String month, String year);

    ArrayList<LeaveRequestMinimalPojo> getLeaveByDateRange(String pisCode, LocalDate fromDate, LocalDate toDate);

    List<DetailPojo> getLeaveByDate(LocalDate fromDate, LocalDate toDate, Boolean forDashboarChart);

    Page<LeaveRequestMainPojo> filterDataBulk(GetRowsRequest paginatedRequest);

    void generateReport(GetRowsRequest paginatedRequest, Integer type);

    void filterExcelReport(ReportPojo reportPojo, HttpServletResponse response);

    List<LeaveOnSameMonthPojo> getLeaveMonthWise();

    List<EmployeeOnLeavePojo> getEmployeeOnLeave();

    List<EmployeeAbsentPojo> getEmployeeOnAbsent();

    List<String> getLeaveInMonth(String month);

    PisLeaveDetailPojo getPisCodeLeaveDetail();

    void checkValidateLeave(LeaveRequestPojo leaveRequestPojo);

    void cancelOngoingLeave(String pisCode) throws ParseException;

}
