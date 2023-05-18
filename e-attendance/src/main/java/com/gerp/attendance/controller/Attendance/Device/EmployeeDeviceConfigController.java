package com.gerp.attendance.controller.Attendance.Device;

import com.gerp.attendance.Pojo.EmployeeDeviceConfigPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.device.EmployeeDeviceConfig;
import com.gerp.attendance.service.EmployeeDeviceConfigService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/employee-device-config")
public class EmployeeDeviceConfigController extends GenericCrudController<EmployeeDeviceConfig, Integer> {
    private EmployeeDeviceConfigService employeeDeviceConfigService;
    private CustomMessageSource customMessageSource;

    public EmployeeDeviceConfigController(EmployeeDeviceConfigService employeeDeviceConfigService, CustomMessageSource customMessageSource) {
        this.employeeDeviceConfigService = employeeDeviceConfigService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.EMPLOYEE_DEVICE_CONFIG_MODULE_NAME;
        this.permissionName= PermissionConstants.ATTENDANCE + "_" + PermissionConstants.EMPLOYEE_DEVICE_CONFIG_SETUP;
    }

    /**
     * This method creates a mapping between employee and device
     * @param employeeDeviceConfigPojo
     * @param bindingResult
     * @return success response with id or failure response
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/save")
    public ResponseEntity<?> create (@Valid @RequestBody EmployeeDeviceConfigPojo employeeDeviceConfigPojo, BindingResult bindingResult) throws BindException {
        if(!bindingResult.hasErrors()) {
            EmployeeDeviceConfig employeeDeviceConfig = employeeDeviceConfigService.save(employeeDeviceConfigPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            employeeDeviceConfig.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }
}
