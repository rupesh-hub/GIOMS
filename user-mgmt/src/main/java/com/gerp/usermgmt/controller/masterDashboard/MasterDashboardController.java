package com.gerp.usermgmt.controller.masterDashboard;

import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.Proxy.MessagingServiceData;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.*;
import com.gerp.usermgmt.pojo.transfer.FileConverterPojo;
import com.gerp.usermgmt.services.MasterDashboard.MasterDashboardService;
import com.gerp.usermgmt.services.delegation.DelegationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class MasterDashboardController extends BaseController {
    private final MasterDashboardService dashboardService;
    private final MessagingServiceData messagingServiceData;
    private final MasterDashboardReport masterDashboardReport;

    public MasterDashboardController(MasterDashboardService dashboardService,
                                     MessagingServiceData messagingServiceData,
                                     MasterDashboardReport masterDashboardReport) {
        this.dashboardService = dashboardService;
        this.messagingServiceData = messagingServiceData;
        this.masterDashboardReport = masterDashboardReport;
        this.moduleName = "Dashboard";
    }

    @PostMapping("/get-top-officeDetail")
    public ResponseEntity<?> getTopOfficeDetail(@RequestBody FilterDataWisePojo filterDataWisePojo) {
        MasterDashboardResponsePojo dashboardPojos = dashboardService.getTopOfficeDetail(filterDataWisePojo);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }

    @PostMapping("/get-top-officeDetail-excel/{lan}")
    public ResponseEntity<?> getTopOfficeDetailExcel(@RequestBody FilterDataWisePojo filterDataWisePojo, @PathVariable("lan") String lan) {

        InputStreamSource resource = dashboardService.getTopOfficeDetailExcel(filterDataWisePojo, lan);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition",
                "attachment; filename=" + "GIOMS_Office_details.xlsx");
        httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.add("Pragma", "no-cache");
        httpHeaders.add("Expires", "0");

//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                .headers(httpHeaders)
//                .body(resource);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

//    @PostMapping(value = "/report/print-top-officeDetail")
//    public ResponseEntity<Resource> getTopOfficeDetail(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType ) {
//
//        FileConverterPojo fileConverterPojo = new FileConverterPojo(dashboardService.generateReport(paginatedRequest , reportType));
//        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
//        assert response != null;
//        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "daily-report_".concat(String.valueOf(Math.random())) + ".pdf")
//                .contentType(MediaType.parseMediaType("application/pdf"))
//                .body(file);
//    }

    @PostMapping("/get-master-dashboard")
    public ResponseEntity<?> getMasterDashboard(@RequestBody OfficeWiseDetailPojo officeWiseDetailPojo) {
        MasterDashboardResponsePojo dashboardPojos = dashboardService.getMasterDashboardData(officeWiseDetailPojo);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dashboardPojos)
        );
    }
}
