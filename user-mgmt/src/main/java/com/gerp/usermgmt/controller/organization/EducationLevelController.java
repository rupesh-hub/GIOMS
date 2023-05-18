package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.services.organization.employee.EducationLevelService;
import com.gerp.usermgmt.services.organization.office.OfficeLevelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/education-level")
public class EducationLevelController extends BaseController {
    private final CustomMessageSource customMessageSource;
    private final EducationLevelService educationLevelService;

    public EducationLevelController(CustomMessageSource customMessageSource, EducationLevelService educationLevelService) {
        this.customMessageSource = customMessageSource;
        this.educationLevelService = educationLevelService;
        this.moduleName = PermissionConstants.OFFICE_LEVEL;
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllOfficeLevel() {
        List<IdNamePojo> s = educationLevelService.getAllEducationLevelMinimalDetail();
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }


}
