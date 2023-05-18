package com.gerp.usermgmt.controller.external;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.pojo.external.TMSClientDetailRequestPojo;
import com.gerp.usermgmt.pojo.external.TMSEmployeePojo;
import com.gerp.usermgmt.pojo.external.TMSOfficePojo;
import com.gerp.usermgmt.pojo.external.TMSScreenModelPojo;
import com.gerp.usermgmt.services.auth.UserService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import com.gerp.usermgmt.services.usermgmt.ModuleService;
import com.gerp.usermgmt.services.usermgmt.PermissionManagementService;
import com.gerp.usermgmt.services.usermgmt.RoleGroupService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/tms/api")
public class TmsController extends BaseController {

    private final OfficeService officeService;

    private final UserService userService;
    private final ModuleService moduleService;
    private final EmployeeService employeeService;
    private final CustomMessageSource customMessageSource;

    private final TokenProcessorService tokenProcessorService;

    private final RoleGroupService roleGroupService;

    private final PermissionManagementService  permissionManagementService;


    public TmsController(OfficeService officeService,
                         UserService userService,
                         ModuleService moduleService,
                         EmployeeService employeeService,
                         CustomMessageSource customMessageSource,
                         TokenProcessorService tokenProcessorService,
                         RoleGroupService roleGroupService,
                         PermissionManagementService permissionManagementService) {
        this.officeService = officeService;
        this.userService = userService;
        this.moduleService = moduleService;
        this.employeeService = employeeService;
        this.customMessageSource = customMessageSource;
        this.tokenProcessorService = tokenProcessorService;
        this.moduleName = PermissionConstants.USER_MODULE_NAME;
        this.roleGroupService = roleGroupService;
        this.permissionManagementService = permissionManagementService;
    }

    @GetMapping("/get/client-list")
    public ResponseEntity<?> getOfficeListForTMSApplication() {
//        List<OfficePojo> office = officeService.getAllGIOMSActiveOffice();
        List<TMSOfficePojo> officePojos = officeService.getAllGIOMSActiveOffice();
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            officePojos)
            );
    }

//    @GetMapping("/office-detail/{officeCode}")
//    public ResponseEntity<?> getOfficeDetailByCode(@PathVariable("officeCode") String officeCode) {
//        List<OfficePojo> office = officeService.officeDetail(officeCode);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
//                            office)
//            );
//    }ModuleCont

    @GetMapping("/get/users")
    public ResponseEntity<?> getEmployeeListByOfficeForTMSApplication(@RequestParam("clientId") String clientId) {
        List<TMSEmployeePojo> employeePojos = employeeService.getEmployeeListDetailByOffice(clientId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        employeePojos)
        );
    }

    @GetMapping("/modules")
    public ResponseEntity<?> getScreenModuleList() {
        List<TMSScreenModelPojo> office = moduleService.getAllModuleScreen();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        office)
        );
    }
    @PostMapping("/module/users")
    public ResponseEntity<?> getScreenModuleListByLoggedInUser(@RequestBody List<Long> userIds) {

        Map<Long,List<RoleGroup>> userIdRolegroupMap = new HashMap<>();
                 userIds.forEach(userId -> {
                     List<RoleGroup> roleGroups = roleGroupService.findRoleGroupListByUsername(userService.findById(userId).getUsername());
                     userIdRolegroupMap.put(userId, roleGroups);
        });
        Map<Long,Set<TMSScreenModelPojo>> tmsScreenModelPojos = moduleService.getModulesByLoggedInUser(userIdRolegroupMap);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        tmsScreenModelPojos)
        );
    }

    @GetMapping("/module")
    public ResponseEntity<?> getScreenModuleDetailById(@RequestParam("moduleId") Long moduleId){
        TMSScreenModelPojo  tmsScreenModelPojo = moduleService.getModuleDetailsById(moduleId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        tmsScreenModelPojo)
        );
    }

    @GetMapping("/user/getEmployeeNameList")
    public ResponseEntity<?> getAllEmployeeNameList(){
        List<TMSEmployeePojo> employeePojos = employeeService.getAllEmployeeList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve",customMessageSource.get(moduleName.toLowerCase())),
                        employeePojos)
        );
    }

    @PostMapping("/get/client-details")
    public ResponseEntity<?> getOfficeDetails(@RequestBody List<String> officeIds){
        List<TMSOfficePojo> officePojos = officeService.getAllOfficeByOfficeCodes(officeIds);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve",customMessageSource.get(moduleName.toLowerCase())),
                        officePojos)
        );    }

    @PostMapping("/get/user-details")
    public ResponseEntity<?> getUserDetails(@RequestBody List<Long> userIds){
        List<TMSEmployeePojo> employeePojos = employeeService.getAllEmployeeById(userIds);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve",customMessageSource.get(moduleName.toLowerCase())),
                        employeePojos)
        );

    }

    @PostMapping("/employee")
    public ResponseEntity<?> getUserDetail(@Valid @RequestBody TMSClientDetailRequestPojo tmsClientDetailRequestPojo, BindingResult bindingResult) throws BindException {
        if(!bindingResult.hasErrors()){
            TMSEmployeePojo tmsEmployeePojo = employeeService.getClientIdByPisCode(tmsClientDetailRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve",customMessageSource.get(moduleName.toLowerCase())),
                            tmsEmployeePojo)
            );        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/modules/getModulesByIds")
    public ResponseEntity<?> getModulesDetailsByIds(@RequestBody List<Long> moduleIds) {
        List<TMSScreenModelPojo> tmsScreenModelPojos = moduleService.getAllModuleByIds(moduleIds);

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve",customMessageSource.get(moduleName.toLowerCase())),
                        tmsScreenModelPojos)
        );
    }






}
