package com.gerp.tms.controller.Phase;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.PermissionConstants;
import com.gerp.tms.pojo.PhaseMemberPojo;
import com.gerp.tms.pojo.request.PhaseRequestPojo;
import com.gerp.tms.pojo.response.PhaseMemberResponsePojo;
import com.gerp.tms.pojo.response.PhaseResponsePojo;
import com.gerp.tms.service.PhaseService;
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
import java.util.List;

@RestController
@RequestMapping("/phase")
public class PhaseController extends BaseController {

    private final PhaseService phaseService;

    public PhaseController(PhaseService phaseService) {
        this.phaseService = phaseService;
        this.moduleName = PermissionConstants.PHASE;
        this.permissionName = "TASKMANAGEMENT_PHASE";
        this.permissionName2 = "TASKMANAGEMENT_PHASE_MEMBER";
    }

    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "Create a new phase", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PhaseRequestPojo.class))})
    public ResponseEntity<?> createPhase(@Valid @RequestBody PhaseRequestPojo phaseRequest, BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
            Long id = phaseService.addPhase(phaseRequest);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                    id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    @Operation(summary = "Update a phase", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PhaseRequestPojo.class))})
    public ResponseEntity<?> updatePhase(@Valid @RequestBody PhaseRequestPojo phaseRequest, BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
            Long id = phaseService.updatePhase(phaseRequest);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a phase by id", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PhaseResponsePojo.class))})
    public ResponseEntity<?> getPhaseDetails(@PathVariable("id") Long id) {
        PhaseResponsePojo phaseResponsePojo =phaseService.getPhaseDetails(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get(moduleName.toLowerCase())),
                           phaseResponsePojo ));
    }

    @GetMapping
    @Operation(summary = "Get all phase", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PhaseResponsePojo.class))})
    public ResponseEntity<?> getPhases() {
        List<PhaseResponsePojo> phaseResponsePojos =phaseService.getPhases();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get(moduleName.toLowerCase())),
                        phaseResponsePojos ));
    }

    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a phase by id", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> deletePhase(@PathVariable Long id) {
        phaseService.deletePhase(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete,customMessageSource.get(moduleName.toLowerCase())),
                        null));
    }

    @PreAuthorize("hasPermission(#this.this.permissionName2,'create')")
    @PutMapping("/add-member")
    @Operation(summary = "Add a phase member to a project", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> addPhaseMemberToProject(@Valid @RequestBody PhaseMemberPojo phaseMemberPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            phaseService.addMembersToProjectPhase(phaseMemberPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add,
                            customMessageSource.get("member")),
                            null));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/project-members")
    @Operation(summary = "Get the phase members of the phase ", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of member id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PhaseMemberResponsePojo.class))})
    public ResponseEntity<?> getPhasesProjectMembers(@RequestParam Integer projectId, @RequestParam(required = false) Integer phaseId) {
        List<PhaseMemberResponsePojo> memberDetailsResponsePojos =phaseService.getPhaseProjectMember(projectId,phaseId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get("member")),
                        memberDetailsResponsePojos));
    }


//    @DeleteMapping("/remove-member")
//    @Operation(summary = "Remove a member from the phase", tags = {"Phase"}, security = {@SecurityRequirement(name = "bearer-key")})
//    public ResponseEntity<?> removeProjectMembers(@RequestParam Integer projectId, @RequestParam Integer phaseId, @RequestParam Integer memberId) {
//        phaseService.removePhaseProjectMember(projectId,phaseId,memberId);
//        return  ResponseEntity.ok(
//                successResponse(customMessageSource.get(CrudMessages.delete,customMessageSource.get(moduleName.toLowerCase())),
//                        null ));
//    }
}
