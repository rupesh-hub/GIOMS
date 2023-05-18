package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.EmployeeType;
import com.gerp.shared.enums.Gender;
import com.gerp.shared.enums.MaritalStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations")
public class OrganizationRestController extends BaseController {
    private final CustomMessageSource customMessageSource;

    public OrganizationRestController(CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.Detail;
    }

    @GetMapping("/genders")
    public ResponseEntity<?> getGenderList() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        Gender.values()));
    }

    @GetMapping("/marital-status")
    public ResponseEntity<?> getMaritalList() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        MaritalStatus.values()));
    }

    @GetMapping("/employee-types")
    public ResponseEntity<?> employeeTypeList() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        EmployeeType.values()));
    }
}
