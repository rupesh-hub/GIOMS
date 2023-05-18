package com.gerp.tms.controller.Committee;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.PermissionConstants;
import com.gerp.tms.pojo.request.CommitteeRequestPojo;
import com.gerp.tms.pojo.response.CommitteeResponsePojo;
import com.gerp.tms.pojo.response.CommitteeWiseProjectResponsePojo;
import com.gerp.tms.service.CommitteeService;
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
import java.util.List;

@RestController
@RequestMapping("/committee")
public class CommitteeController extends BaseController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
        this.moduleName = PermissionConstants.COMMITTEE;
        this.moduleName2 = PermissionConstants.PROJECT;
    }


  //  @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "Create a new committee", tags = {"Committee"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeResponsePojo.class))})
    public ResponseEntity<?> createCommittee(@Valid @RequestBody CommitteeRequestPojo committeeRequestPojo, BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
            int id = committeeService.createCommittee(committeeRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                    id));
        }else {
            throw new BindException(bindingResult);
        }
    }

   // @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    @Operation(summary = "Update a committee", tags = {"Committee"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeResponsePojo.class))})
    public ResponseEntity<?> updatePhase(@Valid @RequestBody CommitteeRequestPojo committeeRequestPojo, BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
            int id = committeeService.updateCommittee(committeeRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a committee by id", tags = {"Committee"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeResponsePojo.class))})
    public ResponseEntity<?> getCommitteeDetails(@PathVariable("id") Integer id) {
        CommitteeResponsePojo committeeResponsePojo =committeeService.getCommitteeDetails(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get(moduleName.toLowerCase())),
                            committeeResponsePojo ));
    }

    @GetMapping
    @Operation(summary = "Get all committees", tags = {"Committee"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeResponsePojo.class))})
    public ResponseEntity<?> getCommittees() {
        List<CommitteeResponsePojo> committeeResponsePojo =committeeService.getCommittees();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get(moduleName.toLowerCase())),
                        committeeResponsePojo ));
    }

    @GetMapping("/project/{committeeId}")
    @Operation(summary = "Get committee wise projects", tags = {"Committee"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeWiseProjectResponsePojo.class))})
    public ResponseEntity<?> getCommitteeWiseProjects(@PathVariable int committeeId) {
        CommitteeWiseProjectResponsePojo projectResponsePojos =committeeService.getCommitteeWiseProject(committeeId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get(moduleName2.toLowerCase())),
                        projectResponsePojos ));
    }

   // @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a committee", tags = {"Committee"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> deletePhase(@PathVariable Integer id) {
        committeeService.deleteCommittee(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete,customMessageSource.get(moduleName.toLowerCase())),
                        null));
    }
}
