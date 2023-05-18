package com.gerp.tms.controller.task;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.PermissionConstants;
import com.gerp.tms.pojo.StatusDecisionPojo;
import com.gerp.tms.pojo.TaskRatingPojo;
import com.gerp.tms.pojo.request.TaskMemberRequestPojo;
import com.gerp.tms.pojo.request.TaskRequestPojo;
import com.gerp.tms.pojo.response.TaskResponsePojo;
import com.gerp.tms.pojo.request.TaskStatusLogRequestPojo;
import com.gerp.tms.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
        this.moduleName = PermissionConstants.TASK;
        this.moduleName2 = PermissionConstants.MEMBER;
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "Create a new task", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public ResponseEntity<?> saveTask(@Valid @ModelAttribute TaskRequestPojo taskRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            TaskResponsePojo taskResponsePojo = taskService.saveTask(taskRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            taskResponsePojo.getId()));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping
    @Operation(summary = "Update a task", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public ResponseEntity<?> updateTask(@Valid @ModelAttribute TaskRequestPojo taskRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            TaskResponsePojo taskResponsePojo = taskService.updateTask(taskRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            taskResponsePojo.getId()));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping
    @Operation(summary = "Get a task list", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public ResponseEntity<?> getTaskList(@RequestParam(required = false) String status,
                                         @RequestParam Integer projectId,
                                         @RequestParam(required = false, defaultValue = "true") Boolean isActive){
            List<TaskResponsePojo> taskResponsePojos = taskService.getAllTask(status,isActive,projectId);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            customMessageSource.get(moduleName.toLowerCase())),
                            taskResponsePojos));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/kasamu-task")
    @Operation(summary = "Get a task list for the employee", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public ResponseEntity<?> getKasamuTaskList(@RequestParam String employeePisCode, @RequestParam     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate fromDate, @RequestParam     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate){
        List<TaskResponsePojo> taskResponsePojos = taskService.getAllTaskForEmployee(employeePisCode,fromDate,toDate);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        taskResponsePojos));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/{id}")
    @Operation(summary = "Get a task by the id", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public ResponseEntity<?> getTaskById(@PathVariable Long id){
        TaskResponsePojo taskResponsePojo = taskService.getTaskById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        taskResponsePojo));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/report-task/{projectId}")
    @Operation(summary = "Get a task for the project", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public ResponseEntity<?> getReportProjectTaskList(@PathVariable Long projectId){
        List<TaskResponsePojo> taskResponsePojos = taskService.getReportProjectTaskList(projectId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        taskResponsePojos));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping("/task-progress-status")
    @Operation(summary = "Update the task progress status ", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public  ResponseEntity<?> updateProgressTaskStatus(@Valid @RequestBody TaskStatusLogRequestPojo taskStatusLogRequestPojo , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            TaskResponsePojo taskResponsePojo = taskService.updateTaskStatusLog(taskStatusLogRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get("status_update")),
                            taskResponsePojo.getId()));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping("/task-status-decision")
    @Operation(summary = "This api is use for approving the task by admin", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponsePojo.class))})
    public  ResponseEntity<?> updateTaskDecision(@Valid @RequestBody StatusDecisionPojo statusDecisionPojo , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            TaskResponsePojo taskResponsePojo = taskService.updateTaskStatusDecision(statusDecisionPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            taskResponsePojo.getId()));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/add-rating")
    @Operation(summary = "This api is use for adding rating to the task when task is completed", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskRatingPojo.class))})
    public  ResponseEntity<?> addTaskRating(@Valid @RequestBody TaskRatingPojo taskRatingPojo , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            long taskResponsePojo = taskService.addRating(taskRatingPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add,
                            customMessageSource.get("rating")),
                            taskResponsePojo));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/add-member")
    @Operation(summary = "This api is use for adding members to the task ", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskRequestPojo.class))})
    public  ResponseEntity<?> addTaskMember(@Valid @RequestBody TaskMemberRequestPojo taskMemberRequestPojo , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            long taskResponsePojo = taskService.addTaskMember(taskMemberRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add,
                            customMessageSource.get(moduleName2.toLowerCase())),
                            taskResponsePojo));
        } else {
            throw new BindException(bindingResult);
        }
    }


    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping("/update-rating")
    @Operation(summary = "This api is use for updating rating only", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskRatingPojo.class))})
    public  ResponseEntity<?> updateTaskRating(@RequestParam int taskRating, @RequestParam long taskId){
            long taskResponsePojo = taskService.updateRating(taskRating,taskId);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get("rating")),
                            taskResponsePojo));
    }


    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/rating/{taskId}")
    @Operation(summary = "Get task rating", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskRatingPojo.class))})
    public ResponseEntity<?> getTaskRating(@PathVariable Long taskId){
        TaskRatingPojo taskRatingPojo = taskService.getTaskRatingById(taskId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get("rating")),
                        taskRatingPojo));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task ", tags = {"Task"}, security = {@SecurityRequirement(name = "bearer-key")})
   public ResponseEntity<?> deleteTask(@PathVariable Long taskId){
       Long id = taskService.deleteTask(taskId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete,
                        customMessageSource.get(moduleName.toLowerCase())),
                        id));
    }

}
