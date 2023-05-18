package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.employee.EmployeeRemarks;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeRemarksPojo;
import com.gerp.usermgmt.services.organization.employee.EmployeeRemarksService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/employee-remarks")
public class EmployeeRemarksController extends BaseController {

    private  final EmployeeRemarksService employeeRemarksService;
    private final CustomMessageSource customMessageSource;

    public EmployeeRemarksController(EmployeeRemarksService employeeRemarksService, CustomMessageSource customMessageSource) {
        this.employeeRemarksService = employeeRemarksService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.USER_MODULE_NAME;
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody EmployeeRemarksPojo employeeRemark, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors() || employeeRemark.getId() != null) {
            EmployeeRemarks employeeRemarks = employeeRemarksService.save( EmployeeRemarks.builder().pisCode(employeeRemark.getPisCode()).
                    remarks(employeeRemark.getRemarks()).employeeStatus(employeeRemark.getEmployeeStatus())
                    .actionDate(employeeRemark.getActionDate() != null ? employeeRemark.getActionDate(): LocalDate.now()).build());
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            employeeRemarks)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody EmployeeRemarksPojo employeeRemark, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            EmployeeRemarks employeeRemarks = employeeRemarksService.update(employeeRemark);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            employeeRemarks.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/get-by-pis-code")
    public ResponseEntity<?> getByPisCode(@RequestParam String pisCode) {
        EmployeeRemarks employeeRemarks = employeeRemarksService.getByPisCode(pisCode);
        if( employeeRemarks != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName.toLowerCase())),
                            employeeRemarks)
            );
        } else {
            throw new NullPointerException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }
}
