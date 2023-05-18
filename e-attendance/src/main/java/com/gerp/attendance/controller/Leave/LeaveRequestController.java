package com.gerp.attendance.controller.Leave;

import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.Proxy.MessagingServiceData;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.leave.LeaveRequest;
import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.attendance.service.LeaveRequestService;
import com.gerp.attendance.util.LeaveReportGenerator;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/leave-request")
public class LeaveRequestController extends BaseController {

    private final LeaveRequestService leaveRequestService;
    private final CustomMessageSource customMessageSource;
    private final MessagingServiceData messagingServiceData;
    private final LeaveReportGenerator leaveReportGenerator;

    public LeaveRequestController(LeaveRequestService leaveRequestService, MessagingServiceData messagingServiceData, CustomMessageSource customMessageSource, LeaveReportGenerator leaveReportGenerator) {
        this.leaveRequestService = leaveRequestService;
        this.messagingServiceData = messagingServiceData;
        this.customMessageSource = customMessageSource;
        this.leaveReportGenerator = leaveReportGenerator;
        this.moduleName = PermissionConstants.LEAVE_REQUEST_MODULE_NAME;
        this.permissionName = PermissionConstants.LEAVE + "_" + PermissionConstants.LEAVE_REQUEST_SETUP;
        this.permissionApproval = PermissionConstants.APPROVAL + "_" + PermissionConstants.LEAVE_APPROVAL;
        this.permissionReport = PermissionConstants.REPORT + "_" + PermissionConstants.LEAVE_REPORT;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute LeaveRequestPojo leaveRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            LeaveRequest leaveRequest = leaveRequestService.save(leaveRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            leaveRequest.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * leave detail ID in leaveRequestPojo.getId()
     * @param leaveRequestUpdatePojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
    @PutMapping
    public ResponseEntity<?> update(@Valid @ModelAttribute LeaveRequestUpdatePojo leaveRequestUpdatePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            LeaveRequestDetail requestedDay = leaveRequestService.update(leaveRequestUpdatePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            requestedDay.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping(value = "/get-by-emp-pis-code")
    public ResponseEntity<?> getLeaveByPisCode() {
        ArrayList<LeaveRequestLatestPojo> leaveRequestLatestPojos = leaveRequestService.getLeaveByPisCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestLatestPojos)
        );

    }

    @PostMapping(value = "/validate-leave")
    public ResponseEntity<?> checkValidateLeave(@Valid @ModelAttribute LeaveRequestPojo leaveRequestPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            leaveRequestService.checkValidateLeave(leaveRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.validate", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            throw new BindException(bindingResult);
        }

    }

    @GetMapping(value = "/get-by-approver-pis-code")
    public ResponseEntity<?> getLeaveByApproverPisCode() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestService.getLeaveByApproverPisCode())
        );

    }

    @GetMapping(value = "/get-by-piscode/leave-detail")
    public ResponseEntity<?> getLeaveDetail() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestService.getPisCodeLeaveDetail())
        );

    }

    @GetMapping(value = "/get-by-office-code")
    public ResponseEntity<?> getLeaveByOfficeCode() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestService.getLeaveByOfficeCode())
        );

    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> getLeaveById(@PathVariable("id") Long detailId) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get",
                                customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestService.getLeaveById(detailId)));

    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        leaveRequestService.deleteLeaveRequest(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        null)
        );
    }

    /**
     * Leave Request APPROVAL API.
     * <p>
     * * This api is used to update approval status.
     * ? {@link ApprovalPojo}
     * ? used for all case
     * </p>
     *
     * @param data
     */
    @PutMapping(value = "/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam(value = "type") ApprovalParamStatus type,
                                          @Valid @ModelAttribute ApprovalPojo data,
                                          BindingResult bindingResult) throws BindException, ParseException {
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
            leaveRequestService.updateStatus(data);

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

    @GetMapping("/year-month")
    public ResponseEntity<?> getLeaveByYearAndMonth(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "month") String month, @RequestParam(name = "year") String year) {
        List<LeaveRequestMinimalPojo> leaveRequestMinimalPojos = leaveRequestService.getLeaveByMonthAndYear(pisCode, month, year);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestMinimalPojos)
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getLeaveByDateRange(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<LeaveRequestMinimalPojo> leaveRequestMinimalPojos = leaveRequestService.getLeaveByDateRange(pisCode, from, to);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestMinimalPojos)
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
                        leaveRequestService.filterDataBulk(paginatedRequest))
        );
    }

    /**
     * Paginated Data Approver
     */
    @PostMapping(value = "/approver/paginated")
    public ResponseEntity<?> getApproverPaginated(@RequestBody GetRowsRequest paginatedRequest) {

        paginatedRequest.setIsApprover(true);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestService.filterDataBulk(paginatedRequest))
        );

    }

    @PostMapping("/leave-history")
    public ResponseEntity<?> getLeaveHistory(@RequestBody final GetRowsRequest paginatedRequest) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestService.getLeaveHistoryByPisCode(paginatedRequest))
        );
    }

    /**
     * Paginated Data Report
     */
    @PostMapping(value = "/report/paginated")
    public ResponseEntity<?> getReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setForReport(true);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveRequestService.filterDataBulk(paginatedRequest))
        );
    }


    @PostMapping(value = "/print-leave-report")
    public ResponseEntity<Resource> getReportPaginatedLeave(@RequestBody GetRowsRequest paginatedRequest, @RequestParam(defaultValue = "0") int reportType) {
        paginatedRequest.setForReport(true);

        FileConverterPojo fileConverterPojo = new FileConverterPojo(leaveReportGenerator.generateReport(paginatedRequest, reportType));
        byte[] response = messagingServiceData.getFileConverter(fileConverterPojo);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "leave-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    /**
     * Get Excel Report
     */
    @PostMapping(value = "/report/excel")
    public ResponseEntity<?> filterExcelReport(@RequestBody ReportPojo reportPojo, HttpServletResponse response) {
        leaveRequestService.filterExcelReport(reportPojo, response);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), null
                )
        );
    }


}
