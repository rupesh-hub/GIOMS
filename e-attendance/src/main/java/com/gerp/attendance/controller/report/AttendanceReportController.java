package com.gerp.attendance.controller.report;

import com.gerp.attendance.Pojo.AttendanceReportType;
import com.gerp.attendance.Pojo.DatePojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.service.AttendanceReportService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/attendance-report")
public class AttendanceReportController extends BaseController {

    private final AttendanceReportService attendanceReportService;

    public AttendanceReportController(AttendanceReportService attendanceReportService) {
        this.attendanceReportService = attendanceReportService;
        this.moduleName = PermissionConstants.ATTENDANCE_REPORT_LIST;
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getAttendanceReportMonthly(@RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                                        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {

        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.report", customMessageSource.get(moduleName)),
                attendanceReportService.getAttendanceReportMonthly(from, to, page, limit)));
    }

    @GetMapping("annual")
    public ResponseEntity<?> getAttendanceReportAnnual(@RequestParam(value = "filterDate") Integer filterDate, @RequestParam(value = "pisCode", required = false) String pisCode) throws Exception {

        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.report", customMessageSource.get(moduleName)),
                attendanceReportService.getAttendanceReportAnnual(filterDate, pisCode)));
    }

    @GetMapping("/test")
    public ResponseEntity<?> getAttendanceReportGeneric(@QueryParam("pisCode") final String pisCode,
                                                        @QueryParam("year") final String year){
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.report", customMessageSource.get(moduleName)),
                attendanceReportService.getAttendanceReportGeneric(pisCode, year)));
    }

    @PostMapping("/monthly/test")
    public ResponseEntity<?> getAttendanceReportMonthlyGeneric(@RequestBody final GetRowsRequest paginatedRequest){
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.report", customMessageSource.get(moduleName)),
                attendanceReportService.getAttendanceReportMonthlyGeneric(paginatedRequest)));
    }
}
