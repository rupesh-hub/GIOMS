package com.gerp.attendance.controller;

import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.attendances.AttendanceType;
import com.gerp.attendance.service.AttendanceTypeService;
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
@RequestMapping("/attendance-type")
public class AttendanceTypeController  extends GenericCrudController<AttendanceType, Integer> {
    private final AttendanceTypeService attendanceTypeService;
    private final CustomMessageSource customMessageSource;


    public AttendanceTypeController(AttendanceTypeService attendanceTypeService, CustomMessageSource customMessageSource) {
        this.attendanceTypeService = attendanceTypeService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.ATTENDANCE_TYPE_MODULE_NAME;
        this.permissionName= PermissionConstants.ATTENDANCE +"_"+PermissionConstants.ATTENDANCE_TYPE;
    }

}
