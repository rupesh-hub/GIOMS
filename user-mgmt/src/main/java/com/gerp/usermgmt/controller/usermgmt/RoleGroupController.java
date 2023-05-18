package com.gerp.usermgmt.controller.usermgmt;

import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.pojo.auth.RolePojo;
import com.gerp.usermgmt.services.usermgmt.RoleGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role-groups")
public class RoleGroupController extends GenericCrudController<RoleGroup, Long> {

    private final RoleGroupService roleGroupService;

    public RoleGroupController(RoleGroupService roleGroupService) {
        this.moduleName = PermissionConstants.ROLE_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.ROLE + "_" + PermissionConstants.ROLE_SETUP;
        this.roleGroupService = roleGroupService;
    }

    @GetMapping("roles")
    public ResponseEntity<?> listMinimal() {
        List<RolePojo> list = roleGroupService.getRolesMinimalDetail();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        list)
        );
    }}
