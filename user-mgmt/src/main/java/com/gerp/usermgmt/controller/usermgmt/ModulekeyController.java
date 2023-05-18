package com.gerp.usermgmt.controller.usermgmt;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.ModuleKeyPojo;
import com.gerp.usermgmt.services.usermgmt.ModuleKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/module-key")
public class ModulekeyController  extends BaseController {

    private final CustomMessageSource customMessageSource;

    private final ModuleKeyService moduleKeyService;

    public ModulekeyController(CustomMessageSource customMessageSource,
                               ModuleKeyService moduleKeyService) {
        this.customMessageSource = customMessageSource;
        this.moduleKeyService = moduleKeyService;
        this.moduleName = PermissionConstants.MODULE_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.MODULE + "_" + PermissionConstants.MODULE_SETUP;
    }

    @GetMapping()
    public ResponseEntity<?> findAll(){
        List<ModuleKeyPojo> moduleKeyPojoList = moduleKeyService.findAll();

        return  new ResponseEntity(successResponse(customMessageSource.get("crud.get_all",
                customMessageSource.get(moduleName)), moduleKeyPojoList), HttpStatus.OK);
    }


}
