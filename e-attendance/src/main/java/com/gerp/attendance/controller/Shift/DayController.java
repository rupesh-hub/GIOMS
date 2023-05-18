package com.gerp.attendance.controller.Shift;

import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.shift.Day;
import com.gerp.attendance.service.DayService;
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
@RequestMapping("/day")
public class DayController extends GenericCrudController<Day, Integer> {

    private final DayService dayService;
    private final CustomMessageSource customMessageSource;

    public DayController(DayService dayService, CustomMessageSource customMessageSource) {
        this.dayService = dayService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.DAY_MODULE_NAME;
        this.permissionName= PermissionConstants.SHIFT+"_"+PermissionConstants.DAY_SETUP;
    }
}
