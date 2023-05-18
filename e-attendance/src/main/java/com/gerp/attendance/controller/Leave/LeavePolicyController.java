package com.gerp.attendance.controller.Leave;

import com.gerp.attendance.Pojo.LeavePolicyPojo;
import com.gerp.attendance.Pojo.LeavePolicyResponsePojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.attendance.service.LeavePolicyService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/leave-policy")
public class LeavePolicyController extends GenericCrudBaseController<LeavePolicy, Long> {

    private final LeavePolicyService leavePolicyService;
    private final CustomMessageSource customMessageSource;

    public LeavePolicyController(LeavePolicyService leavePolicyService, CustomMessageSource customMessageSource) {
        this.leavePolicyService = leavePolicyService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.LEAVE_POLICY_MODULE_NAME;
        this.permissionName = PermissionConstants.LEAVE + "_" + PermissionConstants.LEAVE_POLICY_SETUP;
    }


//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LeavePolicyPojo leavePolicyPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            LeavePolicy leavePolicy = leavePolicyService.save(leavePolicyPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            leavePolicy.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody LeavePolicyPojo leavePolicyPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            LeavePolicy leavePolicy = leavePolicyService.update(leavePolicyPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            leavePolicy.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    // not in use
    @GetMapping(value = "/get-all")
    public ResponseEntity<?> getAllLeavePolicy() {
        ArrayList<LeavePolicyResponsePojo> leavePolicyPojos = leavePolicyService.getAllLeavePolicy();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leavePolicyPojos)
        );

    }

    @GetMapping(value = "/get-applicable/{pisCode}")
    public ResponseEntity<?> getApplicable(@PathVariable("pisCode") String pisCode) {
        List<LeavePolicy> data = leavePolicyService.getApplicable(pisCode,null);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        data)
        );

    }

    @GetMapping(value = "/get-all-applicable")
    public ResponseEntity<?> getAllApplicable() {
        List<LeavePolicy> data = leavePolicyService.getAllApplicable();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        data)
        );

    }

    @GetMapping(value = "/get-applicable")
    public ResponseEntity<?> getApplicableAll() {
        List<LeavePolicy> data = leavePolicyService.getApplicable();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        data)
        );

    }

    @GetMapping(value = "/get-customized-data")
    public ResponseEntity<?> getCustomizedLeavePolicy() {
        ArrayList<LeavePolicyResponsePojo> leavePolicyPojos = leavePolicyService.getCustomizedLeavePolicy();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        leavePolicyPojos)
        );

    }

}
