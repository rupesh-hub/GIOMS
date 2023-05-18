package com.gerp.attendance.controller.Leave;

import com.gerp.attendance.Pojo.LeaveSetupPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.leave.LeaveSetup;
import com.gerp.attendance.service.LeaveSetupService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/leave-setup")
public class LeaveSetupController extends GenericCrudBaseController<LeaveSetup, Long> {

    private final LeaveSetupService leaveSetupService;
    private final CustomMessageSource customMessageSource;

    public LeaveSetupController(LeaveSetupService leaveSetupService, CustomMessageSource customMessageSource) {
        this.leaveSetupService = leaveSetupService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.LEAVE_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.LEAVE+"_"+PermissionConstants.LEAVE_SETUP;
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LeaveSetupPojo leaveSetupPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            LeaveSetup leaveSetup = leaveSetupService.save(leaveSetupPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            leaveSetup.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @Override
    @GetMapping
    public ResponseEntity<?> list() {
        ArrayList<LeaveSetupPojo> leaveSetupPojos = leaveSetupService.getAllLeaveSetupData();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveSetupPojos)
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody LeaveSetupPojo leaveSetupPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            LeaveSetup leaveSetup = leaveSetupService.update(leaveSetupPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            leaveSetup.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping(value = "/get-all")
    public ResponseEntity<?> getAllLeaveSetup() {
        ArrayList<LeaveSetupPojo> leaveSetupPojos = leaveSetupService.getAllLeaveSetup();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leaveSetupPojos)
        );


    }

    @PostMapping("/toggle-status/{id}")
    public ResponseEntity<?> toogleLeaveStatus(@PathVariable("id") Long id) {
        LeaveSetup leaveSetup = leaveSetupService.findById(id);
        if (leaveSetup != null) {
            leaveSetupService.deleteById(id);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(Boolean.TRUE.equals(leaveSetup.getActive())? "crud.inactive": "crud.active", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

}
