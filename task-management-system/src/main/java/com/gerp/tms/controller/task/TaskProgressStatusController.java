package com.gerp.tms.controller.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.PermissionConstants;
import com.gerp.tms.pojo.ProgressUpdatePojo;
import com.gerp.tms.pojo.TaskProgressStatusWithTaskDetailsResponsePojo;
import com.gerp.tms.pojo.request.DecisionByOfficeHeadPojo;
import com.gerp.tms.pojo.request.TaskProgressStatusRequestPojo;
import com.gerp.tms.pojo.response.ApprovalReportDetailPojo;
import com.gerp.tms.pojo.response.MonitoringReportsPojo;
import com.gerp.tms.pojo.response.TaskProgressStatusResponsePojo;
import com.gerp.tms.service.ReportService;
import com.gerp.tms.service.TaskProgressStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.Path;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/taskProgressStatus")
public class TaskProgressStatusController extends BaseController {

    private final TaskProgressStatusService taskProgressStatusService;
    private final ReportService reportService;

    public TaskProgressStatusController(TaskProgressStatusService taskProgressStatusService, ReportService reportService) {
        this.taskProgressStatusService = taskProgressStatusService;
        this.reportService = reportService;
        this.moduleName = PermissionConstants.TASK_PROGRESS_STATUS;
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "Create a new task progress status", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskProgressStatusResponsePojo.class))})
    public ResponseEntity<?> createTaskProgressStatus(@Valid @RequestBody TaskProgressStatusRequestPojo taskProgressStatusRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = taskProgressStatusService.addTaskProgressStatus(taskProgressStatusRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        } else {
            throw new BindException(bindingResult);
        }
    }

    // @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    @Operation(summary = "Update a task progress status", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskProgressStatusResponsePojo.class))})
    public ResponseEntity<?> updateTaskProgressStatus(@Valid @RequestBody TaskProgressStatusRequestPojo taskProgressStatusRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = taskProgressStatusService.updateTaskProgressStatus(taskProgressStatusRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/{id}")
    @Operation(summary = "Get task progress status by id", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskProgressStatusResponsePojo.class))})
    public ResponseEntity<?> getTaskProgressStatus(@PathVariable("id") Long id) {
        TaskProgressStatusResponsePojo taskProgressStatusResponsePojo = taskProgressStatusService.getTaskProgressDetails(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName.toLowerCase())),
                        taskProgressStatusResponsePojo));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping
    @Operation(summary = "Get all task progress status", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskProgressStatusResponsePojo.class))})
    public ResponseEntity<?> getTaskProgressStatus() {
        List<TaskProgressStatusResponsePojo> taskProgressStatusResponsePojoList = taskProgressStatusService.getTaskProgressStatus();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName.toLowerCase())),
                        taskProgressStatusResponsePojoList));
    }

    // @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task progress status", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
   public ResponseEntity<?> deleteTaskProgressStatus(@PathVariable Long id) {
        taskProgressStatusService.deleteTaskProgressStatus(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete, customMessageSource.get(moduleName.toLowerCase())),
                        null));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/get-status-wise-tasks")
    @Operation(summary = "This api is use for getting progress status wise tasks", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskProgressStatusWithTaskDetailsResponsePojo.class))})
    public  ResponseEntity<?> getStatusWiseTasks(@RequestParam(required = false) String status,
                                                 @RequestParam Integer projectId,
                                                 @RequestParam Long phaseId,
                                                 @RequestParam(required = false)  Date startDate,
                                                 @RequestParam(required = false)  Date endDate,
                                                 @RequestParam(required = false) String assigneeId) {
        List<TaskProgressStatusWithTaskDetailsResponsePojo> taskResponsePojo =taskProgressStatusService.getStatusWiseTask(status,projectId,startDate,endDate,assigneeId,phaseId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        taskResponsePojo));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/add-progress")
    @Operation(summary = "Add task progress", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> addTaskProgressMonthly(@Valid @RequestBody ProgressUpdatePojo progressUpdatePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = reportService.addProgressMonthly(progressUpdatePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add,
                            customMessageSource.get("Progress")),
                            id));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping("/submit/{taskId}")
    @Operation(summary = "submit task progress", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> submitTaskProgressMonthly(@PathVariable Long taskId)  {
            Long id = reportService.submitTaskProgressMonthly(taskId);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit,
                            customMessageSource.get("Progress")),
                            id));
    }

    @DeleteMapping("/progress")
    @Operation(summary = "DELETE progress monthly", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> deleteTaskProgressMonthly(@RequestParam Integer progressId, @RequestParam String month)  {
        reportService.deleteTaskProgressMonthly(progressId,month);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete,
                        customMessageSource.get("Progress")),
                        progressId));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping("/decision")
    @Operation(summary = "submit task progress", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> decisionTaskProgressMonthly( @RequestBody List<DecisionByOfficeHeadPojo> decisionByOfficeHeadPojos)  {
        int id = reportService.decisionByOfficeHead(decisionByOfficeHeadPojos);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.approve,
                        customMessageSource.get("Progress")),
                        id));
    }


    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/get-monitoring")
    @Operation(summary = "This api is use for getting monitoring report", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MonitoringReportsPojo.class))})
    public  ResponseEntity<?> getMonitoringReport(@RequestParam String officeCode) {
        List<MonitoringReportsPojo> monitoringReportsPojos =reportService.getMonitoring(officeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.successFullyData),
                        monitoringReportsPojos));
    }

    @GetMapping("/get-approval")
    @Operation(summary = "This api is use for getting employee submitted their task progress for the approval", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApprovalReportDetailPojo.class))})
    public  ResponseEntity<?> getApprovalReport(@RequestParam(required = false,defaultValue = "PENDING") String status,
                                                @RequestParam(required = false,defaultValue = "10") int limit,
                                                @RequestParam(required = false,defaultValue = "1") int page) {
        Page<ApprovalReportDetailPojo> monitoringReportsPojos =reportService.getApprovalReport(status,limit,page);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.successFullyData),
                        monitoringReportsPojos));
    }

    @GetMapping("/get-approval-detail/{id}")
    @Operation(summary = "This api is use for getting employee submitted their task progress for the approval detail view", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MonitoringReportsPojo.class))})
    public  ResponseEntity<?> getApprovalReportDetails(@PathVariable Long id) {
        MonitoringReportsPojo monitoringReportsPojos =reportService.getApprovalReportDetails(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.successFullyData),
                        monitoringReportsPojos));
    }



}

