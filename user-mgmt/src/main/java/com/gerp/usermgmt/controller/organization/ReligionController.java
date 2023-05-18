package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.employee.Religion;
import com.gerp.usermgmt.services.organization.employee.ReligionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/religion")
public class ReligionController extends GenericCrudController<Religion, String> {
    private final ReligionService religionService;
    private final CustomMessageSource customMessageSource;

    public ReligionController(ReligionService religionService, CustomMessageSource customMessageSource) {
        this.religionService = religionService;
        this.moduleName = PermissionConstants.RELIGION_MODULE;
        this.customMessageSource = customMessageSource;
    }
}
