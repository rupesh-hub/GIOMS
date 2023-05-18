package com.gerp.usermgmt.controller.usermgmt;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.auth.RoleGroupScreenMappingPojo;
import com.gerp.usermgmt.pojo.auth.ScreenGroupRoleMappingPojo;
import com.gerp.usermgmt.services.usermgmt.RoleGroupScreenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/role-group-screen-mapping")
public class RoleGroupScreenMappingController extends BaseController {

    private final RoleGroupScreenService roleGroupScreenService;

    public RoleGroupScreenMappingController(RoleGroupScreenService roleGroupScreenService) {
        this.roleGroupScreenService = roleGroupScreenService;
        this.moduleName = PermissionConstants.ROLE_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.ROLE + "_" + PermissionConstants.ROLE_SETUP;
    }

    /**
     *  Use to set Individual screens to role group
     * @param roleGroupScreenMappingPojo
     * @return
     */
    @PostMapping
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    public ResponseEntity<?> createRoleGroupScreenMapping(@Valid @RequestBody RoleGroupScreenMappingPojo roleGroupScreenMappingPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            roleGroupScreenService.createRoleGroupScreenMapping(roleGroupScreenMappingPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())), null)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("role-group/{roleGroupId}")
    public ResponseEntity<?> findByRoleGroupId(@PathVariable Long roleGroupId) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get",
                        customMessageSource.get(moduleName.toLowerCase())),
                        roleGroupScreenService.findByRoleGroupId(roleGroupId))
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        roleGroupScreenService.deleteById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete",
                        customMessageSource.get(moduleName.toLowerCase())),
                        null)
        );
    }

    /**
     *  Use to get Screen Group with active individual screen count
     * @param roleGroupId
     * @return
     */
    @GetMapping(value = "/get-screen-groups/role-group-id/{roleGroupId}")
    public ResponseEntity<?> getScreenGroups(@PathVariable Long roleGroupId){
        List<ScreenGroupRoleMappingPojo> screenGroupRoleMappingPojos = roleGroupScreenService.getScreenGroups(roleGroupId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all",
                        customMessageSource.get("screen.group")),
                        screenGroupRoleMappingPojos)
        );
    }

    /**
     *  Use to get all Individual screen for screen group and checked status for role group
     * @param roleGroupId
     * @param screeGroupId
     * @return
     */
    @GetMapping("/unused-individual-screen/role-group/{roleGroupId}/screen-group-id/{screeGroupId}")
    public ResponseEntity<?> findUnusedIndividualScreen(@PathVariable Long roleGroupId, @PathVariable Long screeGroupId) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all",
                        customMessageSource.get("screen")),
                        roleGroupScreenService.findUnusedIndividualScreen(roleGroupId, screeGroupId))
        );
    }

    /**
     *  Use to get Individual screen mapped to role group
     * @param roleGroupId
     * @param screeGroupId
     * @return
     */
    @GetMapping("role-group/{roleGroupId}/screen-group-id/{screeGroupId}")
    public ResponseEntity<?> findByRoleGroupIdAndScreenGroupId(@PathVariable Long roleGroupId, @PathVariable Long screeGroupId) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get",
                        customMessageSource.get(moduleName.toLowerCase())),
                        roleGroupScreenService.findByRoleGroupIdAndScreenGroupId(roleGroupId, screeGroupId))
        );
    }

    /**
     *  Use to get module detail for screen
     * @param roleGroupId
     * @param screeId
     * @return
     */
    @GetMapping("role-group/{roleGroupId}/screen-id/{screeId}")
    public ResponseEntity<?> findByRoleGroupIdAndScreenIdOld(@PathVariable Long roleGroupId, @PathVariable Long screeId) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get",
                        customMessageSource.get(moduleName.toLowerCase())),
                        roleGroupScreenService.findByRoleGroupIdAndScreenIdOld(roleGroupId, screeId))
        );
    }

    /**
     *  Use to get module detail for screen
     * @param roleGroupId
     * @param screeId
     * @return
     */
    @GetMapping("role-group-id/{roleGroupId}/screen-id/{screeId}")
    public ResponseEntity<?> findByRoleGroupIdAndScreenId(@PathVariable Long roleGroupId, @PathVariable Long screeId) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get",
                        customMessageSource.get(moduleName.toLowerCase())),
                        roleGroupScreenService.findByRoleGroupIdAndScreenId(roleGroupId, screeId))
        );
    }
}
