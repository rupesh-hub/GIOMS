package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.services.organization.employee.EmployeeServiceStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee-service-status")
public class EmployeeServiceStatusController extends BaseController{

        private  final EmployeeServiceStatusService employeeServiceStatusService;
        private final CustomMessageSource customMessageSource;

        public EmployeeServiceStatusController(EmployeeServiceStatusService employeeServiceStatusService, CustomMessageSource customMessageSource) {
            this.employeeServiceStatusService = employeeServiceStatusService;
            this.customMessageSource = customMessageSource;
            this.moduleName = PermissionConstants.SERVICE;
        }

    @GetMapping("/get-all")
    public ResponseEntity<?> getEmployeeListOfLoggedInOffice() {
        List<IdNamePojo> employees = employeeServiceStatusService.findAll();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }
}
