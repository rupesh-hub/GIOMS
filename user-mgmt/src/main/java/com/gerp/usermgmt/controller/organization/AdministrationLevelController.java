package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.administrative.AdministrationLevel;
import com.gerp.usermgmt.services.organization.administration.AdministrationLevelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administration-level")
public class AdministrationLevelController extends GenericCrudController<AdministrationLevel, Integer> {
    private final AdministrationLevelService administrationLevelService;
    private final CustomMessageSource customMessageSource;

    public AdministrationLevelController(AdministrationLevelService administrationLevelService, CustomMessageSource customMessageSource) {
        this.administrationLevelService = administrationLevelService;
        this.moduleName = PermissionConstants.ADMINISTRATION_LEVEL;
        this.customMessageSource = customMessageSource;
    }
}
