package com.gerp.usermgmt.controller.auth;

import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.Privilege;
import com.gerp.usermgmt.services.auth.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/privileges")
public class PrivilegeController extends GenericCrudController<Privilege, Long> {

    private final PrivilegeService privilegeService;

    @Autowired
    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
        moduleName = PermissionConstants.PRIVILEGE_MODULE_NAME;
        permissionName = PermissionConstants.PRIVILEGE + "_" + PermissionConstants.PRIVILEGE_SETUP;
    }
}
