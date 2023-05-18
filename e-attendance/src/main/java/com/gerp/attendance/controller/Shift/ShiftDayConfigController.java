package com.gerp.attendance.controller.Shift;

import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.shift.ShiftDayConfig;
import com.gerp.attendance.service.ShiftDayConfigService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/shift-day-config")
public class ShiftDayConfigController extends GenericCrudController<ShiftDayConfig, Integer> {

    private final ShiftDayConfigService shiftDayConfigService;
    private final CustomMessageSource customMessageSource;

    public ShiftDayConfigController(ShiftDayConfigService shiftDayConfigService, CustomMessageSource customMessageSource) {
        this.shiftDayConfigService = shiftDayConfigService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.SHIFT_DAY_CONFIG_MODULE_NAME;
        this.permissionName= PermissionConstants.SHIFT+"_"+PermissionConstants.SHIFT_SETUP;
    }

//    @PostMapping(value="/save")
//    public ResponseEntity<?> create(@Valid @RequestBody ShiftDayConfigPojo shiftDayConfigPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            ShiftDayConfig shiftDayConfig= shiftDayConfigService.save(shiftDayConfigPojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
//                            shiftDayConfig.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @GetMapping(value="/get-all")
//    public ResponseEntity<?> getAllShiftDayConfig()  {
//        ArrayList<ShiftDayConfigCustomPojo> shiftDayConfigs= shiftDayConfigService.getAllShiftDayConfig();
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        shiftDayConfigs)
//        );
//    }
//
//    @GetMapping(value="/get-by-shiftId/{shiftId}")
//    public ResponseEntity<?> getAllShiftDayConfig(@PathVariable Integer  shiftId)  {
//        ArrayList<ShiftDayConfigCustomPojo> shiftDayConfigCustomPojos= shiftDayConfigService.getByShift(shiftId);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        shiftDayConfigCustomPojos)
//        );
//    }

}
