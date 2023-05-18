package com.gerp.attendance.controller.Dashboard;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.attendance.AttendanceSearchPojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.service.*;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private TokenProcessorService tokenProcessorService;


    private final CustomMessageSource customMessageSource;
    private final EmployeeAttendanceService employeeAttendanceService;
    private final LeaveRequestService leaveRequestService;
    private final KaajRequestService kaajRequestService;
    private final ShiftService shiftService;
    private final DailyLogService dailyLogService;


    public DashboardController(EmployeeAttendanceService employeeAttendanceService, DailyLogService dailyLogService, ShiftService shiftService, LeaveRequestService leaveRequestService, KaajRequestService kaajRequestService, CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
        this.employeeAttendanceService = employeeAttendanceService;
        this.dailyLogService = dailyLogService;
        this.leaveRequestService = leaveRequestService;
        this.shiftService = shiftService;
        this.kaajRequestService = kaajRequestService;
        this.moduleName = PermissionConstants.DASHBOARD_MODULE_NAME;
        this.permissionName = PermissionConstants.DASHBOARD + "_" + PermissionConstants.DASHBOARD_SETUP;
    }

    /**
     * This api provides the information of employee for dashboard
     *
     * @return
     */
    @GetMapping("/get-dashboard-data")
    public ResponseEntity<?> getDashboardData() throws Exception {
        DashboardPojo dashboardPojos = employeeAttendanceService.getDashboardDetails();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }

    @GetMapping("/dashboard-count")
    public ResponseEntity<?> getDashboardCount(@RequestParam int monthId) {
        DashboardCountPojo dashboardPojos = employeeAttendanceService.getDashboardCount(monthId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }


    @GetMapping("/get-dashboard")
    public ResponseEntity<?> getDashboard() throws Exception {
        DashboardDetailPojo dashboardPojos = employeeAttendanceService.getDashboard();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }


    @GetMapping("/get-approval-count")
    public ResponseEntity<?> getDashboardPending() {
        DashboardPendingCount dashboardPendingCount = employeeAttendanceService.getDashboardPendingCount();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPendingCount)
        );
    }


    /**
     * Gets Leave On Each Month By PisCode and OfficeCode
     *
     * @return
     */
    @GetMapping("/get-leave-month")
    public ResponseEntity<?> getLeaveMonthWise() {
        List<LeaveOnSameMonthPojo> leaveOnSameMonthPojos = leaveRequestService.getLeaveMonthWise();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveOnSameMonthPojos)
        );
    }

    /**
     * Gets Employee Information who are on leave
     *
     * @return
     */
    @GetMapping("/get-employee-leave")
    public ResponseEntity<?> getEmployeeOnLeave() {
        List<EmployeeOnLeavePojo> employeeOnLeavePojos = leaveRequestService.getEmployeeOnLeave();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeOnLeavePojos)
        );
    }

    /**
     * Gets Employee Information who are irregular
     *
     * @return
     */
    @GetMapping("/get-irregular-employee")
    public ResponseEntity<?> getIrregularEmployee() {
        List<IrregularEmployeePojo> irregularEmployees = employeeAttendanceService.getIrregularEmployee();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        irregularEmployees
                )
        );
    }

    /**
     * Gets Employee Information Who are on kaaj
     *
     * @return
     */
    @GetMapping("/get-employee-on-kaaj")
    public ResponseEntity<?> getEmployeeOnKaaj() {
        List<EmployeeOnKaajPojo> employeeOnKaajPojos = kaajRequestService.getEmployeeOnKaaj();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeOnKaajPojos)
        );
    }

    @GetMapping("/get-employee-on-absent")
    public ResponseEntity<?> getEmployeeOnAbsent() {
        List<EmployeeAbsentPojo> employeeOnAbsentPojos = leaveRequestService.getEmployeeOnAbsent();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeOnAbsentPojos)
        );
    }

    /**
     * Paginated Daily Log Information of Employee
     *
     * @param paginatedRequest
     * @return
     */
    @PostMapping("/get-piscode-dailylog")
    public ResponseEntity<?> getEmployeeDailyLog(@RequestBody GetRowsRequest paginatedRequest) {
        Page<DailyLogPojo> dailyLogPojos = dailyLogService.getDailyLogDetail(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dailyLogPojos)
        );
    }

    /**
     * This method filters the leave by date-range
     *
     * @param from
     * @param to
     * @return
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getLeaveByDateRange(@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<DetailPojo> leaveMonthwise = leaveRequestService.getLeaveByDate(from, to, false);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveMonthwise)
        );
    }


    /**
     * This api provides the list of dates on which the employee took leave on respective month
     *
     * @param month
     * @return
     */
    @GetMapping("/month")
    public ResponseEntity<?> getDatesInMonth(@RequestParam(name = "month") String month) {
        List<String> dates = leaveRequestService.getLeaveInMonth(month);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dates)
        );
    }

    @GetMapping("/shift")
    public ResponseEntity<?> getShift() {
        ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(tokenProcessorService.getPisCode(), tokenProcessorService.getOfficeCode(), LocalDate.now());

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        shiftPojo)
        );
    }


    @PostMapping("/present-employee-report")
    public ResponseEntity<?> getPresentEmployees(@RequestBody AttendanceSearchPojo attendanceSearchPojo) {

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendanceService.filterEmployeeAttendance(attendanceSearchPojo))
        );
    }

    @GetMapping("/total-employee-list")
    public ResponseEntity<?> getTotalEmployeeList() {
        List<EmployeeAttendancePojo> employeeAttendance = employeeAttendanceService.filterEmployeeList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendance)
        );
    }

    @GetMapping("/get-master-dashboard")
    public ResponseEntity<?> getMasterDashboard(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                @RequestParam(name = "limit") Integer limit,
                                                @RequestParam(name = "pageNo") Integer pageNo,
                                                @RequestParam(name = "by") Integer by,
                                                @RequestParam(name = "type") String type) {
        MasterDashboardResponsePojo dashboardPojos = employeeAttendanceService.getDashboards(fromDate, toDate, limit, pageNo,by, type);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }

    @GetMapping("/get-master-dashboard-excel")
    public ResponseEntity<?> getMasterDashboard(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                @RequestParam(name = "by") Integer by,
                                                @RequestParam(name = "type") String type) {
        MasterDashboardResponsePojo dashboardPojos = employeeAttendanceService.getDashboardsExcel(fromDate, toDate,by, type);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }

    @GetMapping("/get-master-dashboard-total")
    public ResponseEntity<?> getMasterDashboard(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                @RequestParam(name = "officeList") String officeList) {
        MasterDashboardTotalPojo masterDashboardTotal = employeeAttendanceService.getMasterDashboardTotal(fromDate, toDate,officeList);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        masterDashboardTotal)
        );
    }

    @PostMapping("/count/get-kaaj-leave")
    public ResponseEntity<?> getKaajLeaveByOffice(@RequestBody MasterDashboardRequestPojo masterDashboardRequestPojo) {
        MasterDashboardPojo dashboardPojos = employeeAttendanceService.getLeaveKaajCount(masterDashboardRequestPojo.getFromDate(), masterDashboardRequestPojo.getToDate(), masterDashboardRequestPojo.getOfficeCode());
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }
}
