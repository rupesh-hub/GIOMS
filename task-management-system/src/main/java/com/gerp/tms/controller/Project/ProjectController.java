package com.gerp.tms.controller.Project;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.PermissionConstants;
import com.gerp.tms.pojo.request.PhaseDeactivatePojo;
import com.gerp.tms.pojo.request.ProjectDynamicProgressStatusRequestPojo;
import com.gerp.tms.pojo.request.ProjectRequestPojo;
import com.gerp.tms.pojo.response.*;
import com.gerp.tms.service.ProjectService;
import com.gerp.tms.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {

    private final ProjectService projectService;
    private final ReportService reportService;

    public ProjectController(ProjectService projectService, ReportService reportService) {
        this.projectService = projectService;
        this.reportService = reportService;
        this.moduleName = PermissionConstants.PROJECT;
        this.moduleName2 = PermissionConstants.STATUS_UPDATE;
        this.module = PermissionConstants.PHASE;
        this.permissionName = PermissionConstants.PROJECT;
        this.permissionName2 =  PermissionConstants.PROJECT_PHASE;

    }

    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "Create a new project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> addProject(@Valid @ModelAttribute ProjectRequestPojo projectPojo, BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
            ProjectResponsePojo projectResponsePojo = projectService.save(projectPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            projectResponsePojo.getId()));
        }else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    @Operation(summary = "Update a project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> updateProject(@Valid @ModelAttribute ProjectRequestPojo projectPojo, BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
            ProjectResponsePojo project = projectService.update(projectPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            project.getId()));
        }else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
//    @PutMapping("/update-status")
//    @Operation(summary = "Update a project status . This api should be call when admins approve the project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
//    public ResponseEntity<?> updateProjectStatus(@Valid @RequestBody StatusDecisionPojo statusDecisionPojo, BindingResult bindingResult) throws BindException{
//        if (!bindingResult.hasErrors()) {
//            ProjectResponsePojo project = projectService.updateStatus(statusDecisionPojo);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.update,
//                            customMessageSource.get(moduleName2.toLowerCase())),
//                            project.getId()));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }

//    @PreAuthorize("hasPermission(#this.this.permissionName2,'update')")
    @PutMapping("/add-dynamic-progress-status")
    @Operation(summary = "Add a dynamic progress status to a project. This api should be call for adding dynamic progress status to the project. ", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<GlobalApiResponse> addDynamicProgressStatus(@Valid @RequestBody ProjectDynamicProgressStatusRequestPojo progressStatusRequestPojo, BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
           projectService.addProjectTaskStatus(progressStatusRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add,
                            customMessageSource.get("status_update")),
                            null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping
    @Operation(summary = "Get all projects", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> getProjects(@RequestParam(required = false, defaultValue = "id") String sortBy,
                                         @RequestParam(required = false, defaultValue = "desc") String sortByOrder,
                                         @RequestParam(required = false, defaultValue = "") String status,
                                         @RequestParam(required = false,defaultValue = "false")Boolean isCompleted,
                                         @RequestParam(required = false, defaultValue = "true") Boolean isActive){
        List<ProjectResponsePojo> projects = projectService.getAllProject(sortBy,sortByOrder,status,isActive,isCompleted);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                       projects));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/{id}")
    @Operation(summary = "Get a project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> getProjectsDetails(@PathVariable Integer id){
        ProjectResponsePojo projects = projectService.getProjectById(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        projects));
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "delete a project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<GlobalApiResponse> deleteProject(@PathVariable Integer id){
        projectService.deleteProject(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.update,
                        customMessageSource.get(moduleName.toLowerCase())),
                        null));
    }


//    @PreAuthorize("hasPermission(#this.this.permissionName2,'update')")
    @PutMapping("/complete/{id}")
    @Operation(summary = "Update a projectStatus to completed ", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> updateProjectToCompleted(@PathVariable Integer id){
        ProjectResponsePojo projects = projectService.updateProjectToCompleted(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.complete,
                        customMessageSource.get(moduleName.toLowerCase())),
                        projects));
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping("/add-phase")
    @Operation(summary = "Add a phase to the project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> addPhaseToProject(@Valid @RequestBody List<ProjectPhasePojo> projectPhasePojo,BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
            ProjectResponsePojo project = projectService.addPhaseToProject(projectPhasePojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add,
                            customMessageSource.get(module.toLowerCase())),
                            project.getId()));
        }else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping("/mark-star/{id}")
    @Operation(summary = "Book mark a project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> bookMarkedProject(@PathVariable Integer id) {
          Long bookId =  projectService.bookMarkedProject(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            bookId));
    }


    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/member-wise-project")
    @Operation(summary = "Get a project list according to member", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> getMemberWiseProject(@RequestParam(required = false, defaultValue = "") String status,
                                         @RequestParam(required = false,defaultValue = "false")Boolean isCompleted,
                                         @RequestParam(required = false, defaultValue = "true") Boolean isActive){
        MemberWiseProjectResponsePojo projects = projectService.getMemberWiseProject(status,isActive,isCompleted);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        projects));
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/report-project")
    @Operation(summary = "Get a project list according to member", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> getReportProject(@RequestParam(required = false) String status,
                                                  @RequestParam(required = false,defaultValue = "false")Boolean isCompleted,
                                                  @RequestParam(required = false,defaultValue = "10") int limit,
                                                  @RequestParam(required = false,defaultValue = "1") int page,
                                                  @RequestParam(required = false) Boolean programActivity,
                                                  @RequestParam(required = false, defaultValue = "true") Boolean isActive){
        Page<ProjectResponsePojo> projects = projectService.getReportProject(status,isActive,isCompleted,limit,page,programActivity);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        projects));
    }

    @GetMapping("/members")
    @Operation(summary = "Get project member list", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDetailsResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> getProjectMembers(@RequestParam Integer projectId) {
        List<MemberDetailsResponsePojo> memberDetailsResponsePojos =projectService.getProjectMember(projectId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get("member")),
                        memberDetailsResponsePojos));
    }

    @GetMapping("/booked-marked")
    @Operation(summary = "Get booked marked project  list", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> getBookedMarkedProject() {
        List<ProjectResponsePojo> memberDetailsResponsePojos =projectService.getBookedMarkedProjects();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get(moduleName.toLowerCase())),
                        memberDetailsResponsePojos));
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/remove-book-mark/{projectId}")
    @Operation(summary = "remove a book mark ", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<GlobalApiResponse> removeBookMark(@PathVariable Integer projectId){
        projectService.removeBookMark(projectId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.update,
                        customMessageSource.get(moduleName.toLowerCase())),
                        null));
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/remove-phase")
    @Operation(summary = "remove phase from the project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<GlobalApiResponse> removeProjectPhase(@RequestParam Integer projectId, @RequestParam Long phaseId){
       Long id = projectService.removeProjectPhase(projectId,phaseId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.remove,
                        customMessageSource.get(module.toLowerCase())),
                        id));
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping("/deactivate-phase")
    @Operation(summary = "Deactivate the phase ", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<GlobalApiResponse> deactivateProjectPhase(@RequestBody PhaseDeactivatePojo phaseDeactivatePojo,BindingResult bindingResult) throws BindException{
       if (!bindingResult.hasErrors()){
           Long id = projectService.deactivateProjectPhase(phaseDeactivatePojo);
           return  ResponseEntity.ok(
                   successResponse(customMessageSource.get(CrudMessages.update,
                           customMessageSource.get(module.toLowerCase())),
                           id));
       }else {
           throw new BindException(bindingResult);
       }
    }

    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @DeleteMapping("/remove-task-progress")
    @Operation(summary = "remove task progress from the project", tags = {"Project"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<GlobalApiResponse> removeTaskProgress(@RequestParam Integer projectId, @RequestParam Long taskProgressStatusId){
        int id = projectService.removeTaskProgress(projectId,taskProgressStatusId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.remove,
                        customMessageSource.get("status_update")),
                        id));
    }

    @GetMapping("/get-report")
    @Operation(summary = "Get project details with task ", tags = {"Report"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReportProjectTaskDetailsPojo.class))})
    public ResponseEntity<GlobalApiResponse> getReportProject(@RequestParam(required = false) String projectName,
                                              @RequestParam(required = false) LocalDate startDate,

                                              @RequestParam(required = false,defaultValue = "10") int limit,
                                              @RequestParam(required = false,defaultValue = "1") int page,
                                              @RequestParam Long taskId,
                                              @RequestParam(required = false) LocalDate endDate) {
      ReportProjectTaskDetailsPojo memberDetailsResponsePojos =reportService.getProjectReport(projectName,startDate,endDate,limit,page,taskId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.successFullyData),
                        memberDetailsResponsePojos));
    }
}
