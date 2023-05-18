package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.employee.Faculty;
import com.gerp.usermgmt.services.organization.employee.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController extends GenericCrudController<Faculty, String> {
    private final FacultyService facultyService;
    private final CustomMessageSource customMessageSource;

    public FacultyController(FacultyService facultyService, CustomMessageSource customMessageSource) {
        this.facultyService = facultyService;
        this.moduleName = PermissionConstants.FACULTY;
        this.customMessageSource = customMessageSource;
    }

    @GetMapping("/all-minimal")
    public ResponseEntity<?> getFacultiesMinimal() {
        List<IdNamePojo> faculty = facultyService.facultyMinimal();
        if( faculty != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName.toLowerCase())),
                            faculty)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }

    @GetMapping("/faculty-by-education-level")
    public ResponseEntity<?> getFacultiesByEducationLevel(@RequestParam("educationCode") String educationCode) {
        List<IdNamePojo> faculty = facultyService.facultyByEducationLevel(educationCode);
        if( faculty != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName.toLowerCase())),
                            faculty)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }
}
