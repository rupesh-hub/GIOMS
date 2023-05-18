package com.gerp.attendance.controller.Shift;

import com.gerp.attendance.Pojo.shift.OfficeTimePojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.shift.OfficeTimeConfig;
import com.gerp.attendance.model.shift.Shift;
import com.gerp.attendance.service.OfficeTimeConfigService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/office-time-config")
public class OfficeTimeConfigController extends BaseController {
    private final  OfficeTimeConfigService officeTimeConfigService;
    private final CustomMessageSource customMessageSource;

    public OfficeTimeConfigController(OfficeTimeConfigService officeTimeConfigService, CustomMessageSource customMessageSource){
        this.officeTimeConfigService=officeTimeConfigService;
        this.customMessageSource=customMessageSource;
        this.moduleName= PermissionConstants.OFFICE_TIME_CONFIG_MODULE_NAME;
        this.permissionName= PermissionConstants.SHIFT+"_"+PermissionConstants.OFFICE_TIME_CONFIG_SETUP;
    }
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody OfficeTimePojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            OfficeTimeConfig officeTimeConfig = officeTimeConfigService.save(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            officeTimeConfig.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody OfficeTimePojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            OfficeTimeConfig officeTimeConfig = officeTimeConfigService.update(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            officeTimeConfig.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping(value = "/get-by-officeCode/{officeCode}")
    public ResponseEntity<?> getOfficeTimeByOfficeCode(@PathVariable String officeCode) {
       OfficeTimePojo data = officeTimeConfigService.getOfficeTimeByCode(officeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        data
                )
        );
    }

    @GetMapping(value = "/get-all")
    public ResponseEntity<?> getAllOfficeTime() {
        List<OfficeTimePojo> data = officeTimeConfigService.getAllOfficeTime();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        data
                )
        );
    }
}
