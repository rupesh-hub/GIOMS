package com.gerp.attendance.controller.Kaaj;

import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.kaaj.KaajType;
import com.gerp.attendance.service.KaajTypeService;
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
@RequestMapping("/kaaj-type")
public class KaajTypeController extends GenericCrudController<KaajType, Integer> {

    private final KaajTypeService kaajTypeService;
    private final CustomMessageSource customMessageSource;

    public KaajTypeController(KaajTypeService kaajTypeService, CustomMessageSource customMessageSource) {
        this.kaajTypeService = kaajTypeService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.KAAJ_TYPE_MODULE_NAME;
        this.permissionName = PermissionConstants.KAAJ + "_" + PermissionConstants.KAAJ_TYPE_SETUP;
    }

}
