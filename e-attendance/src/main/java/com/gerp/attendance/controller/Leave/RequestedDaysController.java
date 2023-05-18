package com.gerp.attendance.controller.Leave;

import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.attendance.service.RequestedDaysService;
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
@RequestMapping("/requested-days")
public class RequestedDaysController extends GenericCrudController<LeaveRequestDetail, Long> {

    private final RequestedDaysService requestedDaysService;
    private final CustomMessageSource customMessageSource;

    public RequestedDaysController(RequestedDaysService requestedDaysService, CustomMessageSource customMessageSource) {
        this.requestedDaysService = requestedDaysService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.REQUESTED_DAYS_MODULE_NAME;
        this.permissionName= PermissionConstants.LEAVE+"_"+PermissionConstants.REQUESTED_DAYS_SETUP;
    }
}
