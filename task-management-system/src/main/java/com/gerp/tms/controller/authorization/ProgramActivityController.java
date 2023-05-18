package com.gerp.tms.controller.authorization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.PermissionConstants;
import com.gerp.tms.pojo.authorization.ActivityLevelPojo;
import com.gerp.tms.pojo.authorization.ActivityTOProject;
import com.gerp.tms.pojo.authorization.AuthorizationActivityPojo;
import com.gerp.tms.pojo.authorization.HeaderOfficeDetailsPojo;
import com.gerp.tms.pojo.response.ProjectCountPojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;
import com.gerp.tms.service.ProgramService;
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
@RequestMapping("/programs-activity")
public class ProgramActivityController extends BaseController {

    private final ProgramService programService;

    public ProgramActivityController(ProgramService programService) {
        this.programService = programService;
        this.moduleName = "ProgramService";
        this.permissionName= PermissionConstants.PROGRAM_ACTIVITY;
    }

//    //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
//    @GetMapping
//    @Operation(summary = "Get all programs", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthorizationActivityPojo.class))})
//    public ResponseEntity<?> getProjects(@RequestParam(required = false) LocalDate fiscalYear,
//                                         @RequestParam(required = false) String activityName){
//        List<AuthorizationActivityPojo> programs = programService.getProgramsList(fiscalYear,activityName);
//        return  ResponseEntity.ok(
//                successResponse(customMessageSource.get(CrudMessages.get,
//                        customMessageSource.get(moduleName.toLowerCase())),
//                        programs));
//    }


//    @PreAuthorize("hasPermission(#this.this.permissionName,'get')")
    @GetMapping("/activities")
    @Operation(summary = "Get all programs activities", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthorizationActivityPojo.class))})
    public ResponseEntity<?> getProjects(@RequestParam(required = false, defaultValue = "id") String sortBy,
                                         @RequestParam(required = false, defaultValue = "desc") String sortByOrder,
                                         @RequestParam(required = false) String filterByAccountNumber,
                                         @RequestParam(required = false) String filterByHead,
                                         @RequestParam String accountCode,
                                         @RequestParam(required = false,defaultValue = "10") int limit,
                                         @RequestParam(required = false,defaultValue = "1") int page,
                                         @RequestParam(required = false) String fiscalYear,
                                         @RequestParam(required = false) String activityName){
        Page<ActivityLevelPojo> programs = programService.getActivties(fiscalYear,activityName,sortByOrder,sortBy,filterByAccountNumber,page,limit,accountCode,filterByHead);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        programs));
    }

    @GetMapping("/activities/{id}")
    @Operation(summary = "Get all programs activities details", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthorizationActivityPojo.class))})
    public ResponseEntity<?> getPrograms(@PathVariable Integer id){
        ActivityLevelPojo programs = programService.getActivtiesById(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        programs));
    }

    @GetMapping("/officeDetails")
    @Operation(summary = "Get office details", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthorizationActivityPojo.class))})
    public ResponseEntity<?> getOfficeDetails(){
        HeaderOfficeDetailsPojo programs = programService.getHeadingDetails();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        programs));
    }

    @GetMapping("/project-count")
    @Operation(summary = "dashboard api for the program activity", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectCountPojo.class))})
    public ResponseEntity<?> getProjectCounts(@RequestParam String fiscalYear,
                                              @RequestParam(required = false) String responsibleUnit,
                                              @RequestParam(required = false) String months){
        ProjectCountPojo programs = programService.getProjectCountForDashboard(fiscalYear,responsibleUnit,months);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,"Project Count" ),
                        programs));
    }

    @GetMapping("/project-list-dashboard")
    @Operation(summary = "dashboard api for project list", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponsePojo.class))})
    public ResponseEntity<?> getProjectList(@RequestParam String fiscalYear,
                                            @RequestParam(required = false,defaultValue = "10") int limit,
                                            @RequestParam(required = false,defaultValue = "1") int page){
       Page<ProjectResponsePojo> programs = programService.getProjectList(fiscalYear,limit,page);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,"Project list" ),
                        programs));
    }

    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/add-to-project")
    @Operation(summary = "Add activities to project", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> addActivityToProject(@Valid @RequestBody ActivityTOProject activityTOProject, BindingResult bindingResult) throws BindException  {
        if (!bindingResult.hasErrors()) {
            Integer id = programService.addToProject(activityTOProject);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get("programactivity"),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

//    @PostMapping("/temp")
//    @Operation(summary = "temp api for adding data", tags = {"PROGRAMS"}, security = {@SecurityRequirement(name = "bearer-key")})
//    public ResponseEntity<?> temp(@Valid @RequestBody Temp activityTOProject, BindingResult bindingResult) throws BindException  {
//        if (!bindingResult.hasErrors()) {
//            Integer id = programService.addData(activityTOProject);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get("programactivity"),
//                            id));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }


}
