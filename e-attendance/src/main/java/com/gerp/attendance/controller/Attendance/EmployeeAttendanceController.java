package com.gerp.attendance.controller.Attendance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.attendance.LateRemarksPojo;
import com.gerp.attendance.Pojo.attendance.RealTimeAttPojo;
import com.gerp.attendance.Pojo.attendance.RealTimeAttStatus;
import com.gerp.attendance.Pojo.report.*;
import com.gerp.attendance.Pojo.shift.EmployeeRemarksPojo;
import com.gerp.attendance.Proxy.MessagingServiceData;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.service.*;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/employee-attendance")
@Slf4j
public class EmployeeAttendanceController extends BaseController {

    private final EmployeeAttendanceService employeeAttendanceService;
    private final LateAttendanceReportGenerator lateAttendanceReportGenerator;
    private final AbsentEmployeeReportGenerator absentEmployeeReportGenerator;
    private final PresentEmployeeReportGenerator presentEmployeeReportGenerator;
    private final PdfGeneratorService pdfGeneratorService;
    private final MonthlyTemplateService monthlyTemplateService;
    private final MonthlyTemplateDetailService monthlyTemplateDetailService;
    private final MyAttendanceGenerator myAttendanceGenerator;
    private final CustomMessageSource customMessageSource;
    private final MessagingServiceData messagingServiceData;
    private final DailyInformationReportGenerator dailyInformationGenerator;
    private final AttendanceReportService attendanceReportService;


    public EmployeeAttendanceController(EmployeeAttendanceService employeeAttendanceService, MessagingServiceData messagingServiceData,
                                        DailyInformationReportGenerator dailyInformationGenerator,
                                        AbsentEmployeeReportGenerator absentEmployeeReportGenerator,
                                        PdfGeneratorService pdfGeneratorService,
                                        PresentEmployeeReportGenerator presentEmployeeReportGenerator,
                                        LateAttendanceReportGenerator lateAttendanceReportGenerator,
                                        MyAttendanceGenerator myAttendanceGenerator,
                                        MonthlyTemplateService monthlyTemplateService,
                                        MonthlyTemplateDetailService monthlyTemplateDetailService,
                                        CustomMessageSource customMessageSource, AttendanceReportService attendanceReportService) {
        this.employeeAttendanceService = employeeAttendanceService;
        this.lateAttendanceReportGenerator=lateAttendanceReportGenerator;
        this.monthlyTemplateDetailService = monthlyTemplateDetailService;
        this.dailyInformationGenerator=dailyInformationGenerator;
        this.absentEmployeeReportGenerator=absentEmployeeReportGenerator;
        this.pdfGeneratorService = pdfGeneratorService;
        this.presentEmployeeReportGenerator=presentEmployeeReportGenerator;
        this.monthlyTemplateService=monthlyTemplateService;
        this.myAttendanceGenerator=myAttendanceGenerator;
        this.customMessageSource = customMessageSource;
        this.messagingServiceData=messagingServiceData;
        this.attendanceReportService = attendanceReportService;
        this.moduleName = PermissionConstants.EMPLOYEE_ATTENDANCE_MODULE_NAME;
        this.permissionName = PermissionConstants.ATTENDANCE + "_" + PermissionConstants.EMPLOYEE_ATTENDANCE_SETUP;
        this.permissionReport = PermissionConstants.REPORT+"_"+PermissionConstants.ATTENDANCE_REPORT;
        this.permissionReport2 = PermissionConstants.REPORT+"_"+PermissionConstants.SUMMARY_REPORT;
    }

    // this api is used to update the attendance of employee during checking by the pisCode
    @PostMapping("real-time-update")
    public ResponseEntity<?> realTimeAttUpdate(@RequestBody RealTimeAttPojo realTimeAttPojo) throws ParseException {
        log.info("EmpId/time/offCode=> "+realTimeAttPojo.getDeviceId()+"/"+realTimeAttPojo.getCheckTime()+"/"+realTimeAttPojo.getOfficeCode());
        RealTimeAttStatus x = employeeAttendanceService.realTimeAttUpdate(realTimeAttPojo);
        return ResponseEntity.ok("okey");
    }

    // this api is used to initialized all the employee attendance during schedular
    @PostMapping("re-init")
    public ResponseEntity<?> reInit(@RequestParam(required = false) String officeCode,
                                    @RequestParam(required = false) String pisCode,
                                    @RequestParam String startDate,
                                    @RequestParam String endDate) {
        employeeAttendanceService.reInit(officeCode, pisCode, startDate, endDate);
        return ResponseEntity.ok("okey");
    }

    @PutMapping(value = "/late-remarks")
    public ResponseEntity<?> addLateRemarks(@Valid @RequestBody LateRemarksPojo lateRemarksPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            employeeAttendanceService.addLateRemarks(lateRemarksPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * This method gets all emplotendance
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllEmployeeAttendance() {
        ArrayList<EmployeeAttendancePojo> employeeAttendancePojo = employeeAttendanceService.getAllEmployeeAttendance();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendancePojo)
        );
    }

    /**
     * This method gets all employee attendance
     * @return
     */
    @PostMapping("get-monthly-attendance")
    public ResponseEntity<?> getMonthlyAttendance(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojo = employeeAttendanceService.getMonthlyAttendance(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendanceMonthlyReportPojo)
        );
    }



    /**
     * This method gets employee attendance by pisCode
     * @return
     */
    @GetMapping("get-by-pis")
    public ResponseEntity<?> getAllByPisCode() {
        EmployeeAttendanceResponsePojo employeeAttendancePojo = employeeAttendanceService.getEmployeeAttendance();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendancePojo)
        );
    }

    /**
     * This method filter late attendance
     * @param paginatedRequest
     * @return
     */
    @PostMapping(value = "/late-attendance")
    public ResponseEntity<?> getAllLateAttendance(@RequestBody GetRowsRequest paginatedRequest) {
        Page<LateEmployeePojo> lateEmployeePojo = employeeAttendanceService.getAllLateAttendance(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        lateEmployeePojo)
        );
    }

    @PostMapping(value = "/print-late-attendance-report")
    public ResponseEntity<Resource> getReportPaginatedLateAttendance(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType ) throws ParseException {
        paginatedRequest.setIsApprover(true);

        FileConverterPojo fileConverterPojo = new FileConverterPojo(lateAttendanceReportGenerator.generateReport(paginatedRequest , reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "late-attendance-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    /**
     * Excel download for Late Attendance
     * @param reportPojo
     * @param response
     * @return
     */
    @PostMapping("/excel/get-late-attendance")
    public ResponseEntity<?> getLateAttendance(@RequestBody ReportPojo reportPojo,@RequestParam(defaultValue = "0") int reportType,HttpServletResponse response) {
        employeeAttendanceService.getExcelLateAttendance(reportPojo,reportType,response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
                )
        );
    }

    /**
     * This method filter attendance
     * @param paginatedRequest
     * @return
     */
    @PostMapping(value = "/filter/my-attendance")
    public ResponseEntity<?> getMyAttendance(@RequestBody GetRowsRequest paginatedRequest) throws Exception {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
                        employeeAttendanceService.filterMyAttendance(paginatedRequest))
        );
    }

    /**
     * This method provides the employee performance
     * @param employeeCode
     * @return
     */
    @GetMapping(value = "/shift-remarks/{employeeCode}")
    public ResponseEntity<?> getAllEmployeeDetails(@PathVariable String employeeCode) {
        List<EmployeeRemarksPojo> employeeRemarksPojo=employeeAttendanceService.getEmployeeDetails(employeeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        employeeRemarksPojo
                )
        );
    }

    /**
     * This method filter the employee attendance by date
     * @param date
     * @return
     */
    @GetMapping("get-by-date/{date}")
    public ResponseEntity<?> getAllByDate(@PathVariable("date") LocalDate date) {
        ArrayList<EmployeeAttendancePojo> employeeAttendancePojo = employeeAttendanceService.getByDate(date);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendancePojo)
        );
    }

    /**
     *
     * @param paginatedRequest
     * @return
     */
    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendancePojo> page = employeeAttendanceService.getAllAttendanceByDate(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    /**
     * Paginated for Daily Information
     * @param paginatedRequest
     * @return
     */
    @PostMapping("get-daily-information")
    public ResponseEntity<?> getDailyInformation(@RequestBody GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> employeeAttendancePojo = employeeAttendanceService.getDailyInformation(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendancePojo)
        );
    }

    @PostMapping("/get-absent-employee")
    public ResponseEntity<?> getAbsentEmployee(@RequestBody GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> employeeAttendancePojo = employeeAttendanceService.getAbsentData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendancePojo)
        );
    }

    @PostMapping(value = "/print-absent-report")
    public ResponseEntity<Resource> getReportPaginatedAbsentEmployee(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType ) {

        FileConverterPojo fileConverterPojo = new FileConverterPojo(absentEmployeeReportGenerator.generateReport(paginatedRequest , reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "daily-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @PostMapping("/get-present-employee")
    public ResponseEntity<?> getPresentEmployee(@RequestBody GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> employeeAttendancePojo = employeeAttendanceService.getPresentData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendancePojo)
        );
    }

    @PostMapping(value = "/print-present-report")
    public ResponseEntity<Resource> getReportPaginatedPresent(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType ) {

        FileConverterPojo fileConverterPojo = new FileConverterPojo(presentEmployeeReportGenerator.generateReport(paginatedRequest , reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "daily-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }
    /**
     * Paginated for Daily Information
     * @param paginatedRequest
     * @return
     */
    @PostMapping("get-daily-information-report")
    public ResponseEntity<?> getDailyInformationForReport(@RequestBody GetRowsRequest paginatedRequest) {
        Page<DailyInformationPojo> employeeAttendancePojo = employeeAttendanceService.getDailyInformationForReport(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendancePojo)
        );
    }

    @PostMapping(value = "/print-daily-report")
    public ResponseEntity<Resource> getReportPaginatedInformation(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType ) throws ParseException {
        paginatedRequest.setForReport(true);

        FileConverterPojo fileConverterPojo = new FileConverterPojo(dailyInformationGenerator.generateReport(paginatedRequest , reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "daily-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }



    /**
     * Excel download for Daily Information
     * @param reportPojo
     * @param response
     * @return
     */
    @PostMapping("/excel/get-daily-information")
    public ResponseEntity<?> getExcelDailyInformation(@RequestBody ReportPojo reportPojo,@RequestParam(defaultValue = "0") int reportType,HttpServletResponse response) {
        employeeAttendanceService.getExcelDailyInformation(reportPojo,reportType,response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
                )
        );
    }


    /**
     * Paginated Data Employee
     */
    @PostMapping(value = "/employee/paginated")
    public ResponseEntity<?> getEmployeePaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForReport(true);
        Page<EmployeeAttendanceReportDataPojo> page = employeeAttendanceService.filterDataPaginatedEmployee(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }




    /**
     * Paginated Data Report for employee Attendance
     */
    @PostMapping(value = "/report/paginated")
    public ResponseEntity<?> getReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForReport(true);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendanceService.filterDataPaginated(paginatedRequest))
        );
    }

    @PostMapping(value = "/print-myattendance-report")
    public ResponseEntity<Resource> getReportPaginatedMyAttendance(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType ) throws ParseException {
        paginatedRequest.setForReport(true);
        paginatedRequest.setIsApprover(true);

        FileConverterPojo fileConverterPojo = new FileConverterPojo(myAttendanceGenerator.generateReport(paginatedRequest , reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "daily-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    /**
     * Paginated Monthly Attendance Report
     */
    @PostMapping(value = "/report-monthly/paginated")
    public ResponseEntity<?> getMonthlyReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        //TODO: previous one
//        paginatedRequest.setForLeave(true);
//        paginatedRequest.setForKaaj(true);
//        paginatedRequest.setForHoliday(true);
//        paginatedRequest.setForReportDetail(true);
//        Page<EmployeeAttendanceMonthlyReportPojo> page = employeeAttendanceService.filterDataPaginatedMonthly(paginatedRequest);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
//                        page)
//        );

        //TODO: optimized one
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.report", customMessageSource.get(moduleName)),
                attendanceReportService.getAttendanceReportMonthlyGeneric(paginatedRequest)));
    }

    /**
     * Paginated Monthly Attendance Report
     */
    @PostMapping(value = "/report-monthly-detail/paginated")
    public ResponseEntity<?> getMonthlyReportDetailPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForLeave(true);
        paginatedRequest.setForKaaj(true);
        paginatedRequest.setForHoliday(true);
        paginatedRequest.setForReportDetail(true);
        Page<EmployeeAttendanceMonthlyReportPojo> page = employeeAttendanceService.filterDataPaginatedDetailMonthly(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
                        page)
        );
    }

    @PostMapping("/report-monthly-detail")
    public ResponseEntity<Resource> generateMonthlyDetail(@RequestBody GetRowsRequest paginatedRequest, HttpServletRequest request, @RequestParam(defaultValue = "0") int reportType) {
        paginatedRequest.setForLeave(true);
        paginatedRequest.setForDaily(true);
        paginatedRequest.setForKaaj(true);
        paginatedRequest.setForHoliday(true);
        paginatedRequest.setForReportDetail(true);
        byte[] bytes = pdfGeneratorService.generateMonthlyDetailPdf(paginatedRequest,String.valueOf(reportType));
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= monthly-report-detail.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @PostMapping("/report-monthly-detail-new")
    public ResponseEntity<Resource> generateMonthlyDetailNew(@RequestBody GetRowsRequest paginatedRequest, HttpServletRequest request, @RequestParam(defaultValue = "0") int reportType) {
        paginatedRequest.setForLeave(true);
        paginatedRequest.setForDaily(true);
        paginatedRequest.setForKaaj(true);
        paginatedRequest.setForHoliday(true);
        paginatedRequest.setForReportDetail(true);
        byte[] bytes = monthlyTemplateDetailService.generateMonthlyDetailPdf(paginatedRequest,String.valueOf(reportType));
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= monthly-report-detail.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }



    /**
     * Paginated Yearly Employee Attendance
     */
    @GetMapping(value = "/yearly-employee-attendance")
    public ResponseEntity<?> getYearlyEmployeeAttendance(@RequestParam(name = "year") String year,
                                                         @RequestParam(name = "pisCode") String pisCode) {

        //TODO: previous
//        List<EmployeeAttendanceMonthlyReportPojo>attendanceYearly = employeeAttendanceService.getYearlyAttendance(year,pisCode);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
//                        attendanceYearly)
//        );

        //TODO: Optimized
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
                attendanceReportService.getAttendanceReportGeneric(pisCode, year)));
    }

    //TODO need to work on yearly excel report
    /**
     * Paginated Yearly Employee Attendance
     */
    @GetMapping(value = "/yearly-employee-attendance/excel")
    public ResponseEntity<?> getYearlyEmployeeAttendanceExcel(@RequestParam(name = "year") Integer year,@RequestParam(name = "pisCode") String pisCode, @RequestParam(defaultValue = "0") int reportType,HttpServletResponse response) {
        List<EmployeeAttendanceMonthlyReportPojo>attendanceYearly = employeeAttendanceService.getYearlyAttendanceExcel(year,pisCode,reportType,response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
                        attendanceYearly)
        );
    }
    /**
     * Monthly Attendance Excel Report Generated
     * @param reportPojo
     * @param response
     * @return
     */
    @PostMapping(value = "/report-monthly/excel")
    public ResponseEntity<?> filterExcelMonthlyReport(@RequestBody ReportPojo reportPojo, HttpServletResponse response) {
        employeeAttendanceService.filterExcelMonthlyReport(reportPojo, response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
                )
        );
    }

    @PostMapping("/report-monthly")
    public ResponseEntity<Resource> generateMonthly(@RequestBody GetRowsRequest paginatedRequest, HttpServletRequest request, @RequestParam(defaultValue = "0") int reportType) {
        paginatedRequest.setForLeave(true);
        paginatedRequest.setForDaily(true);
        paginatedRequest.setForKaaj(true);
        paginatedRequest.setForHoliday(true);
        paginatedRequest.setForReportDetail(true);
        byte[] bytes = monthlyTemplateService.generateMonthlyPdf(paginatedRequest,String.valueOf(reportType));
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= monthly-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @PostMapping("/report-monthly-new")
    public ResponseEntity<Resource> generateMonthlyCheck(@RequestBody GetRowsRequest paginatedRequest, HttpServletRequest request, @RequestParam(defaultValue = "0") int reportType) {
        paginatedRequest.setForLeave(true);
        paginatedRequest.setForDaily(true);
        paginatedRequest.setForKaaj(true);
        paginatedRequest.setForHoliday(true);
        paginatedRequest.setForReportDetail(true);
        byte[] bytes = monthlyTemplateService.generateMonthlyPdf(paginatedRequest,String.valueOf(reportType));
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= monthly-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }
//    @PostMapping("/report-monthly-detail")
//    public ResponseEntity<Resource> generateMonthlyDetail(@RequestBody GetRowsRequest paginatedRequest, HttpServletRequest request, @RequestParam(defaultValue = "0") int reportType) {
//        byte[] bytes = pdfGeneratorService.generateMonthlyDetailPdf(paginatedRequest,String.valueOf(reportType));
//        assert bytes != null;
//        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= monthly-detail-report.pdf")
//                .contentType(MediaType.parseMediaType("application/pdf"))
//                .body(file);
//    }

    /**
     * Monthly Leave Excel Report Generated
     * @param reportPojo
     * @param response
     * @return
     */
    @PostMapping(value = "/report-monthly-leave/excel")
    public ResponseEntity<?> filterExcelMonthlyLeaveReport(@RequestBody ReportPojo reportPojo, HttpServletResponse response) {
        reportPojo.setForLeave(true);
        employeeAttendanceService.filterExcelMonthlyReport(reportPojo, response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
                )
        );
    }

    /**
     * Monthly Kaaj Excel Report Generated
     * @param reportPojo
     * @param response
     * @return
     */
    @PostMapping(value = "/report-monthly-kaaj/excel")
    public ResponseEntity<?> filterExcelMonthlyKaajReport(@RequestBody ReportPojo reportPojo, HttpServletResponse response) {
        reportPojo.setForKaaj(true);
        employeeAttendanceService.filterExcelMonthlyReport(reportPojo, response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
                )
        );
    }



    /**
     * Paginated Monthly Leave Report
     */
    @PostMapping(value = "/report-monthly-leave/paginated")
    public ResponseEntity<?> getMonthlyLeaveReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForLeave(true);
        Page<EmployeeAttendanceMonthlyReportPojo> page = employeeAttendanceService.filterDataPaginatedMonthly(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
                        page)
        );
    }

    /**
     * Paginated Monthly Kaaj Report
     */
    @PostMapping(value = "/report-monthly-kaaj/paginated")
    public ResponseEntity<?> getMonthlyKaajReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForKaaj(true);
        Page<EmployeeAttendanceMonthlyReportPojo> page = employeeAttendanceService.filterDataPaginatedDetailMonthly(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("monthly")),
                        page)
        );
    }

    /**
     * Get Excel Report for attendance report
     */
    @PostMapping(value = "/report/excel")
    public ResponseEntity<?> filterExcelReport(@RequestBody ReportPojo reportPojo, @RequestParam(defaultValue = "0") int reportType,HttpServletResponse response) {
        employeeAttendanceService.filterEmployeeExcelReport(reportPojo, reportType,response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
                )
        );
    }

    /**
     * Paginated Data Summary Report
     */
    @PostMapping(value = "/report-summary/paginated")
    public ResponseEntity<?> getSummaryReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForReport(true);
        Page<EmployeeAttendanceSummaryDataPojo> page = employeeAttendanceService.filterSummaryPagination(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    /**
     * Get Excel Summary Report
     */
    @PostMapping(value = "/report-summary/excel")
    public ResponseEntity<?> filterExcelSummaryReport(@RequestBody ReportPojo reportPojo,@RequestParam(defaultValue = "0") int reportType, HttpServletResponse response) {
        employeeAttendanceService.filterExcelSummaryReport(reportPojo,reportType, response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
                )
        );
    }

    @GetMapping(value = "/absence-list")
    public ResponseEntity<?> findMonthlyAbsentList(@RequestParam int monthId) {
        List<String> employeeAttendancePojos = employeeAttendanceService.findAbsentList(monthId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),employeeAttendancePojos
                )
        );
    }

    @GetMapping(value = "/irregular-attendance")
    public ResponseEntity<?> findMonthlyIrregularDataAttendance(@RequestParam int monthId) {
        Object employeeAttendancePojos = employeeAttendanceService.findIrregularAttendanceByMonth(monthId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),employeeAttendancePojos
                )
        );
    }

    @GetMapping(value = "/irregular-attendance-all")
    public ResponseEntity<?> findMonthlyIrregularDataAttendanceAll(@RequestParam(value = "date", required = false) String date) {

        LocalDate dateOf = DateUtil.isNotNullAndEmpty(date) ? LocalDate.parse(date) : null;
        Object employeeAttendancePojos = employeeAttendanceService.findIrregularAttendanceByMonthAll(dateOf);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),employeeAttendancePojos
                )
        );
    }

    @GetMapping(value = "/year-start-end")
    public ResponseEntity<?> getStartEndDate(@RequestParam Integer nepaliYear) {
        DateListingPojo dateListingPojo = employeeAttendanceService.findByYear(nepaliYear);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),dateListingPojo
                )
        );
    }

    //    /**
//     * Get Excel Report
//     */
//    @PostMapping(value = "/report/excel")
//    public ResponseEntity<?> filterExcelReport(@RequestBody ReportPojo reportPojo, HttpServletResponse response) {
//        employeeAttendanceService.filterExcelReport(reportPojo, response);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),null
//                )
//        );
//    }

    /**
     * employee attendance and leave update when holiday update
     * @param date
     * @return
     */

    @PostMapping(value = "holidayUpdated")
    public ResponseEntity<?> holidayUpdated(@RequestParam String date) {
        employeeAttendanceService.holidayUpdated(date);
        return ResponseEntity.ok("holiday updated");
    }
}
