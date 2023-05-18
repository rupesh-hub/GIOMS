package com.gerp.usermgmt.controller.usermgmt;


import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.cache.UserPrivilegeCacheRepo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.pojo.ModulePrivilageGroup;
import com.gerp.usermgmt.pojo.ModuleWiseIndividualPrivilegeSaveDto;
import com.gerp.usermgmt.pojo.ModuleWisePrivilegeSaveDto;
import com.gerp.usermgmt.pojo.RoleGroupScreenModulePrivilegeDto;
import com.gerp.usermgmt.pojo.auth.MobileScreenPrivilegePojo;
import com.gerp.usermgmt.services.auth.UserService;
import com.gerp.usermgmt.services.usermgmt.PermissionManagementService;
import com.gerp.usermgmt.services.usermgmt.RoleGroupService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permission-managements")
public class PermissionManagementController extends BaseController {


    private final PermissionManagementService permissionManagementService;
    private final UserService userService;
    private final RoleGroupService roleGroupService;
//    private final SetupDataLoader setupDataLoader;
    private final UserPrivilegeCacheRepo userPrivilegeCacheRepo;
    @Autowired private TokenProcessorService tokenProcessorService;
//    private final UserScreenGroupPrivilegeCacheRepo userScreenGroupPrivilegeCacheRepo;

    public PermissionManagementController(PermissionManagementService permissionManagementService,
                                          UserService userService,
                                          RoleGroupService roleGroupService,
//                                          SetupDataLoader setupDataLoader,
                                          UserPrivilegeCacheRepo userPrivilegeCacheRepo
//                                          UserScreenGroupPrivilegeCacheRepo userScreenGroupPrivilegeCacheRepo
    ) {
        this.permissionManagementService = permissionManagementService;
        this.userService = userService;
        this.roleGroupService = roleGroupService;
//        this.setupDataLoader = setupDataLoader;
        this.userPrivilegeCacheRepo = userPrivilegeCacheRepo;
//        this.userScreenGroupPrivilegeCacheRepo = userScreenGroupPrivilegeCacheRepo;
        this.moduleName = PermissionConstants.USER_PRIVILEGE_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.ROLE + "_" + PermissionConstants.ROLE_SETUP;
    }


    //    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    //    public ResponseEntity<?> savePermissionForTheRoleGroup(@Valid @RequestBody List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList, BindingResult bindingResult) throws BindException {
    //        if (!bindingResult.hasErrors()) {
    //            String response = permissionManagementService.saveRolePermission(roleGroupScreenModulePrivilegeDtoList);
    //            if (response != null) {
    //                return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission saved successfully", null), HttpStatus.OK);
    //            } else {
    //                return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.FAIL, "Permission saved failed", null), HttpStatus.INTERNAL_SERVER_ERROR);
    //            }
    //
    //        } else {
    //            throw new BindException(bindingResult);
    //        }
    //    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PostMapping(value = "/save/screen-wise", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePermissionForTheRoleGroupScreenWise(@Valid @RequestBody ModuleWisePrivilegeSaveDto moduleWisePrivilegeSaveDto,
                                                                     BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            String response = permissionManagementService.saveRolePermissionScreenWise(moduleWisePrivilegeSaveDto);
            if (response != null) {
//                setupDataLoader.loadDataToRedis();
                return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission saved successfully", null), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.FAIL, "Permission saved failed", null), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PostMapping(value = "/save/screen-wise-individual", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveIndividualPermission(@Valid @RequestBody ModuleWiseIndividualPrivilegeSaveDto moduleWiseIndividualPrivilegeSaveDto,
                                                                     BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            permissionManagementService.saveIndividualPermission(moduleWiseIndividualPrivilegeSaveDto);
            return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission saved successfully", null), HttpStatus.OK);
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PostMapping(value = "/save/screen-wise-all", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAllPermission(@Valid @RequestBody ModuleWiseIndividualPrivilegeSaveDto moduleWiseIndividualPrivilegeSaveDto,
                                                                     BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            permissionManagementService.saveAllPermission(moduleWiseIndividualPrivilegeSaveDto);
            return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "All Permission saved successfully", null), HttpStatus.OK);
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PostMapping(value = "/save/screen-wise-all-module", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAllPermissionAllModule(@Valid @RequestBody ModuleWiseIndividualPrivilegeSaveDto moduleWiseIndividualPrivilegeSaveDto,
                                                                     BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            permissionManagementService.saveAllPermissionAllModule(moduleWiseIndividualPrivilegeSaveDto);
            return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "All Permission saved successfully", null), HttpStatus.OK);
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/role-group/{id}")
    public ResponseEntity<?> getByRoleGroupId(@PathVariable("id") Long uuid) {
        List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList = permissionManagementService.findRoleGroupScreenModulePrivilegeDtoListByRoleGroupId(uuid);
        if (roleGroupScreenModulePrivilegeDtoList != null) {
            return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission retrieved success", roleGroupScreenModulePrivilegeDtoList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.FAIL, "Permission not found", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/role-group/{roleGroupId}/screen-id/{screenId}")
    public ResponseEntity<?> getByRoleGroupIdAndScreenId(@PathVariable("roleGroupId") Long roleGroupId, @PathVariable("screenId") Long screenId) {
        List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList = permissionManagementService.
                findRoleGroupScreenModulePrivilegeDtoListByRoleGroupIdAndScreenId(roleGroupId, screenId);
        if (roleGroupScreenModulePrivilegeDtoList != null) {
            return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission retrieved success", roleGroupScreenModulePrivilegeDtoList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.FAIL, "Permission not found", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * This api returns the screen wise privilege of current logged in user
     * The response is in the format that the front end wants in tree structure
     * screen -> module -> privileges
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/screen-wise/privilege-list")
    public ResponseEntity<?> screenWisePrivileges(final Principal principal) throws Exception {
        String username = tokenProcessorService.isDelegated() ? tokenProcessorService.getPisCode() : principal.getName();
        List<RoleGroup> roleGroupList = roleGroupService.findRoleGroupListByUsername(username);
        String keyName = generateKey(roleGroupList);
        Map<String, Map<String, Map<String, Object>>> screenWisePrivileges = null;
//        PrivilegeCacheDto privilegeCacheDto = userPrivilegeCacheRepo.findByKey(keyName);
//        if (privilegeCacheDto == null) {
            List<Long> roleGroupIdList = roleGroupList.stream().map(idNamePojo -> idNamePojo.getId()).collect(Collectors.toList());
            screenWisePrivileges = permissionManagementService.screenWisePrivileges(roleGroupIdList);
//            privilegeCacheDto = new PrivilegeCacheDto().builder().key(keyName).value(screenWisePrivileges).build();
//            userPrivilegeCacheRepo.saveScreenWisePrivilege(privilegeCacheDto);
//        } else {
//            screenWisePrivileges = privilegeCacheDto.getValue();
//        }
        return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission retrieved success", screenWisePrivileges), HttpStatus.OK);
    }

    @GetMapping("/screen-group-wise/privilege-list")
    public ResponseEntity<?> screenGroupWisePrivileges(Principal principal) throws Exception {
        String username = tokenProcessorService.isDelegated() ? tokenProcessorService.getPisCode() : principal.getName();
        List<RoleGroup> roleGroupList = roleGroupService.findRoleGroupListByUsername(username);
        String keyName = generateKey(roleGroupList);
        Map<String,Map<String, Map<String, Map<String, Object>>>> screenGroupWisePrivileges = null;
        List<Long> roleGroupIdList = roleGroupList.stream().map(idNamePojo -> idNamePojo.getId()).collect(Collectors.toList());
        screenGroupWisePrivileges = permissionManagementService.screeGroupWisePrivileges(keyName, roleGroupIdList);
        return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission retrieved success", screenGroupWisePrivileges), HttpStatus.OK);
    }

    @GetMapping("/screen-group-wise/mobile-privilege-list")
    public ResponseEntity<?> screenGroupWiseMobilePrivileges(Principal principal) throws Exception {
        String username = tokenProcessorService.isDelegated() ? tokenProcessorService.getPisCode() : principal.getName();
        List<RoleGroup> roleGroupList = roleGroupService.findRoleGroupListByUsername(username);
        String keyNameMobile = generateKey(roleGroupList);
        List<MobileScreenPrivilegePojo> screenGroupWiseMobilePrivileges = null;
        List<Long> roleGroupIdList = roleGroupList.stream().map(idNamePojo -> idNamePojo.getId()).collect(Collectors.toList());
        screenGroupWiseMobilePrivileges = permissionManagementService.screeGroupWiseMobilePrivileges(keyNameMobile, roleGroupIdList);
        return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission retrieved success", screenGroupWiseMobilePrivileges), HttpStatus.OK);
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PostMapping("/module-privilege")
    public ResponseEntity<?> screenWirePrivileges(@RequestBody List<ModulePrivilageGroup> modulePrivilegeGroups) {
        List<User> users = permissionManagementService.findByGroupAndScreen(modulePrivilegeGroups);
        return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission retrieved success", users), HttpStatus.OK);
    }


    /**
     * Get the privileges list according to  the role group name
     */

    @GetMapping("/screen-wise/privilege-list/role-group/{roleGroup}")
    public ResponseEntity<?> screenWisePrivilegesByRoleGroup(@PathVariable("roleGroup") String roleGroupName) throws Exception {
        RoleGroup roleGroup = roleGroupService.findRoleGroupByKey(roleGroupName);
        List<Long> roleList = new ArrayList<>();
        roleList.add(roleGroup.getId());
        Object object = permissionManagementService.screenWisePrivileges(roleList);
        return new ResponseEntity<>(new GlobalApiResponse(ResponseStatus.SUCCESS, "Permission retrieved success", object), HttpStatus.OK);
    }

    private String generateKey(List<RoleGroup> roleGroupList) {
        String keyName = "";
        for (RoleGroup roleGroup : roleGroupList) {
            keyName += roleGroup.getKey();
        }
        return keyName;
    }

    private String generateKeyString(List<String> roleNameList) {
        String keyName = "";
        for (String roleName : roleNameList) {
            keyName += roleName;
        }
        return keyName;
    }
}
