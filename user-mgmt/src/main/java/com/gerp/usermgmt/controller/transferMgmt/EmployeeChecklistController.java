package com.gerp.usermgmt.controller.transferMgmt;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.pojo.transfer.EmployeeChecklistPojo;
import com.gerp.usermgmt.pojo.transfer.TransferPojo;
import com.gerp.usermgmt.repo.transfer.EmployeeRequestCheckListRepo;
import com.gerp.usermgmt.services.transfer.EmployeeChecklistService;
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

@RestController
@RequestMapping("/employee-transfer-checklist")
public class EmployeeChecklistController extends BaseController {

    private final EmployeeChecklistService employeeChecklistService;

    public EmployeeChecklistController(EmployeeChecklistService employeeChecklistService) {
        this.employeeChecklistService = employeeChecklistService;
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "add employee transfer request form checklist", tags = {"TRANSFER MANAGEMENT CHECKLIST"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> addCheckList(@Valid @ModelAttribute EmployeeChecklistPojo employeeRequestCheckList, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create, customMessageSource.get("checkList")),
                            employeeChecklistService.addCheckList(employeeRequestCheckList)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping
    @Operation(summary = "update employee transfer request form checklist", tags = {"TRANSFER MANAGEMENT CHECKLIST"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> updateCheckList(@Valid @ModelAttribute EmployeeChecklistPojo employeeRequestCheckList, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create, customMessageSource.get("checkList")),
                            employeeChecklistService.updateCheckList(employeeRequestCheckList)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "add employee transfer request form checklist", tags = {"TRANSFER MANAGEMENT CHECKLIST"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeChecklistPojo.class))})
    public ResponseEntity<?> getCheckList(){
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create, customMessageSource.get("checkList")),
                            employeeChecklistService.getCheckList()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete employee transfer request form checklist", tags = {"TRANSFER MANAGEMENT CHECKLIST"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> deleteCheckList(@PathVariable int id){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.create, customMessageSource.get("checkList")),
                        employeeChecklistService.deleteCheckList(id)));
    }

}
