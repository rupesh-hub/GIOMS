package com.gerp.attendance.controller.Kaaj;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.kaaj.report.KaajReportData;
import com.gerp.attendance.Pojo.report.KaajReportGenerator;
import com.gerp.attendance.Pojo.report.KaajSummaryReportPojo;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.Proxy.MessagingServiceData;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.service.KaajRequestService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ApprovalParamStatus;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.ApprovalPojo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/kaaj-request")
public class KaajRequestController extends BaseController {

    private final KaajRequestService kaajRequestService;
    private final CustomMessageSource customMessageSource;
    private final MessagingServiceData messagingServiceData;
    private final KaajReportGenerator kaajReportGenerator;
    private final KaajSummaryReportPojo kaajSummaryReportPojo;

    public KaajRequestController(KaajRequestService kaajRequestService,
                                 MessagingServiceData messagingServiceData,
                                 KaajReportGenerator kaajReportGenerator,
                                 KaajSummaryReportPojo kaajSummaryReportPojo,
                                 CustomMessageSource customMessageSource) {
        this.kaajRequestService = kaajRequestService;
        this.customMessageSource = customMessageSource;
        this.kaajSummaryReportPojo = kaajSummaryReportPojo;
        this.kaajReportGenerator = kaajReportGenerator;
        this.messagingServiceData = messagingServiceData;
        this.moduleName = PermissionConstants.KAAJ_REQUEST_MODULE_NAME;
        this.permissionName = PermissionConstants.KAAJ + "_" + PermissionConstants.KAAJ_REQUEST_SETUP;
        this.permissionApproval = PermissionConstants.APPROVAL + "_" + PermissionConstants.KAAJ_APPROVAL;
        this.permissionReport = PermissionConstants.REPORT + "_" + PermissionConstants.KAAJ_REPORT;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute KaajRequestPojo kaajRequestPojoParam, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            KaajRequest kaajRequest = kaajRequestService.save(kaajRequestPojoParam);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            kaajRequest.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @ModelAttribute KaajRequestPojo kaajRequestPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            KaajRequest kaajRequest = kaajRequestService.update(kaajRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            kaajRequest.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllKaajRequest() {
        List<KaajRequestCustomPojo> kaajRequestCustomPojos = kaajRequestService.getAllKaajRequest();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestCustomPojos)
        );

    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> getKaajRequestById(@PathVariable Long id) {

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestService.getKaajRequestById(id))
        );
    }

    @GetMapping(value = "/get-by-emp-pis-code")
    public ResponseEntity<?> getKaajRequestByPisCode() {
        List<KaajRequestCustomPojo> kaajRequestCustomPojos = kaajRequestService.getKaajRequestByPisCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestCustomPojos)
        );
    }

    @GetMapping(value = "/get-by-approver-pis-code")
    public ResponseEntity<?> getLeaveByApproverPisCode() {
        List<KaajRequestCustomPojo> leaveRequestLatestPojos = kaajRequestService.getKaajByApproverPisCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestLatestPojos)
        );

    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        kaajRequestService.deleteKajRequest(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        null)
        );
    }

    /**
     * Kaaj Request APPROVAL API.
     * <p>
     * * This api is used to update approval status.
     * ? {@link ApprovalPojo}
     * ? used for all case
     * </p>
     *
     * @param data
     */
    @PutMapping(value = "/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam(value = "type") ApprovalParamStatus type, @Valid @ModelAttribute ApprovalPojo data, BindingResult bindingResult) throws BindException, ParseException {
        if (!bindingResult.hasErrors()) {

            if (type == null) {
                type = ApprovalParamStatus.update;
            }
            switch (type) {
                case update:
                    if (!data.getStatus().validateUpdate())
                        throw new RuntimeException(customMessageSource.get("user.can.only.cancel"));
                    break;
                case approve:
                    if (!data.getStatus().validateApprove())
                        throw new RuntimeException(customMessageSource.get("approver.cannot.cancel"));
                    break;
                case review:
                    if (!data.getStatus().validateReview())
                        throw new RuntimeException(customMessageSource.get("reviewer.can.only.forward"));
                    break;

                case revert:
                    if (!data.getStatus().validateRevert())
                        throw new RuntimeException(customMessageSource.get("reviewer.can.only.revert"));
                    break;
            }

            data.setModule(this.module);
            data.setModuleName(this.moduleName);
            kaajRequestService.updateStatus(data);

            switch (data.getStatus()) {
                case A:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.approved", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                case R:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.rejected", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                case F:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                case C:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.cancel", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                case RV:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.revert", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                default:
                    return ResponseEntity.ok(
                            errorResponse(customMessageSource.get("invalid.action"),
                                    null)
                    );
            }
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/get-paper-report-data/{kaajRequestId}")
    public ResponseEntity<?> getPaperReportData(@PathVariable Long kaajRequestId) {
        KaajReportData data = kaajRequestService.getPaperReportData(kaajRequestId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        data)
        );
    }

    @GetMapping("/year-month")
    public ResponseEntity<?> getKaajByYearAndMonth(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "month") String month, @RequestParam(name = "year") String year) {
        List<KaajRequestMinimalPojo> kaajRequestMinimalPojos = kaajRequestService.getKaajByMonthAndYear(pisCode, month, year);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestMinimalPojos)
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getKaajByDateRange(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<KaajRequestMinimalPojo> kaajRequestMinimalPojos = kaajRequestService.getKaajByDateRange(pisCode, from, to);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestMinimalPojos)
        );

    }

    /**
     * getting kaaj history of particular user by pis code
     * @param rowsRequest
     * @return
     */
    @PostMapping("/kaaj-history")
    public ResponseEntity<?> getKaajHistoryByPisCode(@RequestBody final GetRowsRequest rowsRequest) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestService.getKaajHistoryByPisCode(rowsRequest))
        );
    }

    /**
     * Paginated Data Employee
     */
    @PostMapping(value = "/employee/paginated")
    public ResponseEntity<?> getEmployeePaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setIsApprover(false);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestService.filterData(paginatedRequest))
        );
    }

    /**
     * Paginated Data Approver
     */
    @PostMapping(value = "/approver/paginated")
    public ResponseEntity<?> getApproverPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setIsApprover(true);
//        paginatedRequest.setForReport(false);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestService.filterData(paginatedRequest))
        );
    }

    /**
     * Paginated Data Report
     */
    @PostMapping(value = "/report/paginated")
    public ResponseEntity<?> getReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForReport(true);
//        paginatedRequest.setIsApprover(true);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestService.filterData(paginatedRequest))
        );
    }

    @PostMapping(value = "/print-Kaaj-report")
    public ResponseEntity<Resource> getReportPaginatedLeave(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType) {
        paginatedRequest.setForReport(true);

        FileConverterPojo fileConverterPojo = new FileConverterPojo(kaajReportGenerator.generateReport(paginatedRequest, reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "kaaj-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    /**
     * Get Excel Report
     */
    @PostMapping(value = "/report/excel")
    public ResponseEntity<?> filterExcelReport(@RequestBody ReportPojo reportPojo, HttpServletResponse response) {
        kaajRequestService.filterExcelReport(reportPojo, response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), null
                )
        );
    }

    /**
     * Paginated Data Report
     */
    @PostMapping(value = "/kaaj-summary/paginated")
    public ResponseEntity<?> getKaajSummayReport(@RequestBody GetRowsRequest paginatedRequest) throws ParseException {
        paginatedRequest.setForReport(true);
//        paginatedRequest.setIsApprover(true);

        AttendanceMonthlyReportPojoPagination page = kaajRequestService.filterKaajSummary(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @PostMapping(value = "/print-Kaaj-summary-report")
    public ResponseEntity<Resource> getReportSummaryPaginated(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType) throws ParseException {
        paginatedRequest.setForReport(true);

        FileConverterPojo fileConverterPojo = new FileConverterPojo(kaajSummaryReportPojo.generateReport(paginatedRequest, reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);

        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "kaaj-report_summary" + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }
}
