package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.attendance.*;
import com.gerp.attendance.Pojo.report.*;
import com.gerp.attendance.Pojo.shift.EmployeeRemarksPojo;
import com.gerp.attendance.Pojo.shift.ShiftDetailPojo;
import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EmployeeAttendanceService extends GenericService<EmployeeAttendance, Long> {
    void save();

    void updateAttendanceSchedular();

    List<EmployeeAttendance> saveEmployeeAttendance();

    Map<String,List<EmployeeAttendancePojo>> filterEmployeeAttendance(AttendanceSearchPojo attendanceSearchPojo);
    List<EmployeeAttendancePojo> filterEmployeeList();

    EmployeeAttendance update(EmployeeAttendancePojo employeeAttendancePojo);

    ArrayList<EmployeeAttendancePojo> getAllEmployeeAttendance();
    Page<LateEmployeePojo> getAllLateAttendance(GetRowsRequest paginatedRequest);

    EmployeeAttendanceResponsePojo getByPisCode(String pisCode);
    Page<EmployeeAttendanceMonthlyReportPojo> getMonthlyAttendance(GetRowsRequest paginatedRequest);
    EmployeeAttendanceResponsePojo getEmployeeAttendance();
    ArrayList<EmployeeAttendancePojo> getByDate(LocalDate date);
    List<EmployeeRemarksPojo> getEmployeeDetails(String pisCode);

   Page<EmployeeAttendancePojo> getAllAttendanceByDate(GetRowsRequest paginatedRequest);

    DashboardPojo getDashboardDetails() throws Exception;

    DashboardDetailPojo getDashboard() throws Exception;

    EmployeeAttendancePage<EmployeeAttendanceReportDataPojo> filterDataPaginated(GetRowsRequest paginatedRequest);
    Page<EmployeeAttendanceReportDataPojo> filterDataPaginatedEmployee(GetRowsRequest paginatedRequest);
    void filterEmployeeExcelReport(ReportPojo reportPojo, int reportType,HttpServletResponse response);

    Page<EmployeeAttendanceSummaryDataPojo>filterSummaryPagination(GetRowsRequest paginatedRequest);

    List<IrregularEmployeePojo> getIrregularEmployee();

    void saveApproveEmployeeAttendance(ApproveAttendancePojo data) throws ParseException;

    void initializeAllAttendanceData();

    Page<EmployeeAttendanceMonthlyReportPojo> filterDataPaginatedMonthly(GetRowsRequest paginatedRequest);

    Page<EmployeeAttendanceMonthlyReportPojo> filterDataPaginatedDetailMonthly(GetRowsRequest paginatedRequest);


    Page<EmployeeAttendancePojo>filterMyAttendance(GetRowsRequest paginatedRequest) throws Exception;

    void getExcelDailyInformation(ReportPojo reportPojo, int reportType,HttpServletResponse response);

    void getExcelLateAttendance(ReportPojo reportPojo,int reportType, HttpServletResponse response);

   void  filterExcelSummaryReport(ReportPojo reportPojo,int reportType, HttpServletResponse response);

//    Map<String, List<LateEmployeePojo>> findIrregularAttendanceByMonth(int monthId);
    Map<String, Object> findIrregularAttendanceByMonth(int monthId);
    List<String> findAbsentList(int monthId);

   Page<DailyInformationPojo> getDailyInformation(GetRowsRequest paginatedRequest);

    Page<DailyInformationPojo> getAbsentData(GetRowsRequest paginatedRequest);

    Page<DailyInformationPojo> getPresentData(GetRowsRequest paginatedRequest);

   Page<DailyInformationPojo> getDailyInformationForReport(GetRowsRequest paginatedRequest);

    void filterExcelMonthlyReport(ReportPojo reportPojo, HttpServletResponse response);

    Set<Long> getShiftId(ShiftDetailPojo shiftDetailPojo);

    void addLateRemarks(LateRemarksPojo lateRemarksPojo);

    RealTimeAttStatus realTimeAttUpdate(RealTimeAttPojo realTimeAttPojo) throws ParseException;

    List<EmployeeAttendanceMonthlyReportPojo> getYearlyAttendance(Integer year,String pisCode);

    List<EmployeeAttendanceMonthlyReportPojo> getYearlyAttendanceExcel(Integer year,String pisCode,int reportType,HttpServletResponse response);

    DashboardPendingCount getDashboardPendingCount();

    DashboardCountPojo getDashboardCount(int id);

    void reInit(String officeCode, String pisCode, String startDate, String endDate);

    MasterDashboardResponsePojo getDashboards(String fromDate, String toDate,Integer limit,Integer pageNo, Integer by, String type);
    MasterDashboardResponsePojo getDashboardsExcel(String fromDate, String toDate, Integer by, String type);

    MasterDashboardPojo getLeaveKaajCount(String fromDate, String toDate, List<String> officeCode);

    DateListingPojo findByYear(Integer year);

    Object findIrregularAttendanceByMonthAll(LocalDate date);

    MasterDashboardTotalPojo getMasterDashboardTotal(String fromDate, String toDate, String officeList);

    void holidayUpdated(String date);
}
