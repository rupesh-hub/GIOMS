package com.gerp.dartachalani.controller;

import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.service.DashboardService;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.utils.CrudMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Times;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {
    private final DashboardService dashboardService;

    public enum TYPE {RECEIVED, SENDER}


    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
        this.moduleName = "dashboard";
    }

    @GetMapping
    @Operation(summary = "dashboard api", tags = {"Darta-Dashboard"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DashboardCountPojo.class))})
    public ResponseEntity<GlobalApiResponse> dashboardApi(@RequestParam(required = false) String sectionId,
                                                          @RequestParam(required = false) String employeeCode,
                                                          @RequestParam(required = false) String officeCode,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                          @RequestParam(required = false) String fiscalYear,
                                                          @RequestParam(required = false) String toOfficeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                        dashboardService.getDashboardCount(sectionId, employeeCode, officeCode, startDate, endDate, fiscalYear, toOfficeCode)));
    }

    @GetMapping("/bar-chart")
    @Operation(summary = "dashboard api for bar chart", tags = {"Darta-Dashboard"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DashboardCountPojo.class))})
    public ResponseEntity<GlobalApiResponse> dashboardPieChartApi(@RequestParam(required = false) String officeCode,
                                                                  @RequestParam(required = false) String sectionId,
                                                                  @RequestParam String period,
                                                                  @RequestParam(required = false) String toOfficeCode) {
        DashboardCountPojo dashboardCountPojo = dashboardService.circleGraphDashboard(officeCode, period, true, sectionId, toOfficeCode);
        dashboardCountPojo.setPeriod(period);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), dashboardCountPojo));
    }

    @GetMapping("/sidebar-detail")
    @Operation(summary = "sidebar api for getting details", tags = {"Sidebar"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DashboardCountPojo.class))})
    public ResponseEntity<GlobalApiResponse> sidebarCount(@RequestParam String sectionId, @RequestParam(required = false) String fiscalYearCode) {
        DashboardCountPojo dashboardCountPojo = dashboardService.sidebarCount(sectionId, fiscalYearCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), dashboardCountPojo));
    }


    @GetMapping("/master-dashboard")
    public ResponseEntity<?> getMasterDashboard(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                @RequestParam(name = "limit") Integer limit,
                                                @RequestParam(name = "pageNo") Integer pageNo,
                                                @RequestParam(name = "officeList") String officeList,
                                                @RequestParam(name = "by", required = false) Integer by,
                                                @RequestParam(name = "type", required = false) String type) {
        MasterDashboardResponsePojo masterDatabasePojo = dashboardService.getTotalDartaCount(fromDate, toDate, limit, pageNo, officeList, by, type);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), masterDatabasePojo));
    }

    @GetMapping("/master-dashboard-excel")
    public ResponseEntity<?> getMasterDashboardExcel(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                @RequestParam(name = "officeList") String officeList,
                                                     @RequestParam("by") Integer by,
                                                     @RequestParam("type") String type) {
        MasterDashboardResponsePojo masterDatabasePojo = dashboardService.getTotalDartaCountExcel(fromDate, toDate, officeList, by, type);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), masterDatabasePojo));
    }

    @PostMapping("/count/darta-chalani-tippani")
    public ResponseEntity<?> getMasterDashboard(@RequestBody MasterDashboardRequestPojo masterDashboardRequestPojo) {
        MasterDataPojo masterDatabasePojo = dashboardService.getDartaCountByOfficeCode(masterDashboardRequestPojo.getFromDate(), masterDashboardRequestPojo.getToDate(), masterDashboardRequestPojo.getOfficeCode());
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), masterDatabasePojo));
    }

    @GetMapping("/master-dashboard-total")
    public ResponseEntity<?> getMasterDashboardTotal(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                     @RequestParam(name = "officeList") String officeList) {
        MasterDashboardTotalPojo masterDashboardTotal = dashboardService.getMasterDashboardTotal(fromDate, toDate, officeList);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), masterDashboardTotal));
    }

}
