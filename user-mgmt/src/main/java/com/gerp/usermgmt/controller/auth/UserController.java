package com.gerp.usermgmt.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gerp.shared.constants.OperationConstants;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.json.ApiDetail;
import com.gerp.shared.utils.UtilityService;
import com.gerp.usermgmt.annotations.UserRoleLogExecution;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.pojo.auth.*;
import com.gerp.usermgmt.pojo.device.PisCodeToDeviceMapperPojo;
import com.gerp.usermgmt.services.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends BaseController {

    private final UserService userService;
    private final UtilityService utilityService;
    @Autowired
    private EmployeeMapper employeeMapper;

    public UserController(UserService userService, UtilityService utilityService) {
        this.userService = userService;
        this.utilityService = utilityService;
        this.moduleName = PermissionConstants.USER_MODULE_NAME;
        this.permissionName = PermissionConstants.USER + "_" + PermissionConstants.USER_SETUP;
    }

    @PostMapping("/authorize")
    public ResponseEntity<?> authorizeUser(HttpServletRequest httpServletRequest , @Valid @RequestBody ApiDetail apiDetail, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            userService.isUserAuthorized(apiDetail)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * <p>
     * This method is used to create internal user.
     * </p>
     *
     * @param userAddPojo
     * @return global response
     */
    @UserRoleLogExecution
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody UserAddPojo userAddPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            User user = userService.createInternalUser(userAddPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            user.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * <p>
     * This method is used to update internal user.
     * </p>
     *
     * @param userUpdatePojo
     * @return global response
     */
    @UserRoleLogExecution
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdatePojo userUpdatePojo, BindingResult bindingResult) throws BindException, JsonProcessingException {
        log.info("---------------  updating user/role info ---------------");
        if (!bindingResult.hasErrors()) {
            userService.updateInternalUser(userUpdatePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            userUpdatePojo
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * <p>
     * This method is used to get internal user detail by id.
     * </p>
     *
     * @param id
     * @return global response
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        UserResponsePojo userResponsePojo = userService.findByIdCustom(id);
        return ResponseEntity.ok(successResponse(
                customMessageSource.get("crud.get", customMessageSource.get(moduleName)), userResponsePojo));
    }

    /**
     * <p>
     * This method is used to toggle user active status.
     * </p>
     *
     * @param id
     * @return global response
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok(successResponse(
                customMessageSource.get("crud.get", customMessageSource.get(moduleName)), null));
    }

    /**
     * <p>
     * Get user detail form token.
     * </p>
     *
     * @return global response
     */
    @GetMapping(value = "/get-user-info")
    public ResponseEntity<?> getUserInfo() {
        log.info("getting user info"+"/get-userinfo");
        UserResponsePojo userResponsePojo = userService.getUserInfo();
        log.info(userResponsePojo.toString());
        return ResponseEntity.ok(successResponse(
                customMessageSource.get("crud.get", customMessageSource.get(moduleName)), userResponsePojo));
    }

    /**
     * <p>
     * This method is used to update password by user.
     * </p>
     *
     * @param passwordPojo,authentication
     * @return global response
     */
    @PostMapping(value = "update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid PasswordPojo passwordPojo, Authentication authentication, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            userService.updatePassword(passwordPojo, authentication);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("user.passwordUpdate"),
                            null)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * <p>
     * This method is used to update password of client user by admin.
     * </p>
     *
     * @param passwordUpdatePojo
     * @return global response
     */
//    @PreAuthorize("{hasPermission(#this.this.permissionName,'update')}")
    @GetMapping(value = "update-password-by-admin")
    public ResponseEntity<?> updatePasswordByAdmin(@RequestBody @Valid PasswordUpdatePojo passwordUpdatePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            userService.updatePassword(passwordUpdatePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("user.passwordUpdate"),
                            null)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * <p>
     * This method is used to get isPasswordChanged Status.
     * </p>
     *
     * @param auth
     * @return global response
     */
    @GetMapping("/check-password")
    public ResponseEntity<?> checkPassword(Authentication auth) throws Exception {
        boolean status = userService.checkPassword(auth);
        return ResponseEntity.ok(successResponse(
                customMessageSource.get("success.retrieve", customMessageSource.get(moduleName)),
                status
        ));
    }

    /**
     * <p>
     * This method is used to update password by user for first time login.
     * </p>
     *
     * @param passwordPojo,authentication
     * @return global response
     */
//    @PreAuthorize("{hasPermission(#this.this.permissionName,'update')}")
    @PutMapping("/update-password-one-time")
    public ResponseEntity<?> updatePasswordOneTime(@RequestBody @Valid PasswordUpdateOneTimePojo passwordPojo, Authentication authentication, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            userService.updatePasswordOneTime(passwordPojo, authentication);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("user.passwordUpdate"),
                            null)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Paginated Data with filter
     */
    @PostMapping(value = "filter-paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setIsApprover(true);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserResponsePojo> page = userService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    /**
     * <p>
     * This method is used to map piscode to device id.
     * </p>
     *
     * @param pisCodeToDeviceMapperPojo
     * @return global response
     */
//    @PreAuthorize("{hasPermission(#this.this.permissionName,'create')}")
    @PostMapping(value = "/save-device-id")
    public ResponseEntity<?> saveDeviceId(@Valid @RequestBody PisCodeToDeviceMapperPojo pisCodeToDeviceMapperPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            userService.saveDeviceId(pisCodeToDeviceMapperPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.save", "Device Mapping"),
                            null)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * <p>
     * Get user device id if exists.
     * </p>
     *
     * @return global response
     */
    @GetMapping(value = "/get-device-id/{pisCode}")
    public ResponseEntity<?> getDeviceId(@PathVariable String pisCode) {
        Long deviceId = userService.getDeviceIdByPisCode(pisCode);
        return ResponseEntity.ok(successResponse(
                customMessageSource.get("crud.get", "Device Id"), deviceId));
    }

    /**
     * change password by admin
     *
     * @param passwordChangePojo
     * @return
     */
    @PutMapping(value = "/change-password-by-admin")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangePojo passwordChangePojo, BindingResult bindingResult) throws BindException, JsonProcessingException {
        if (!bindingResult.hasErrors()) {
            // todo create standard format of logging
            log.info("-----------api for changing password------------ \n endpoint: /change-password-by-admin" );
            boolean result = userService.changePassword(passwordChangePojo);
            return ResponseEntity.ok(successResponse(

                    customMessageSource.get("user.passwordUpdate"), null));
        } else {
            throw new BindException(bindingResult);
        }
    }
}
