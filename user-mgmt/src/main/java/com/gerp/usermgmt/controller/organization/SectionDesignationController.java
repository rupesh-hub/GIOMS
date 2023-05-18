package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.annotations.SectionDesignationLogExecution;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.enums.SectionDesignationActivity;
import com.gerp.usermgmt.model.employee.SectionDesignation;
import com.gerp.usermgmt.pojo.organization.office.SectionDesignationPojo;
import com.gerp.usermgmt.services.organization.office.SectionDesignationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/section-designation")
public class SectionDesignationController extends BaseController {
    private final CustomMessageSource customMessageSource;
    private final SectionDesignationService sectionDesignationService;

    public SectionDesignationController(CustomMessageSource customMessageSource, SectionDesignationService sectionDesignationService) {
        this.customMessageSource = customMessageSource;
        this.sectionDesignationService = sectionDesignationService;
        this.moduleName = PermissionConstants.SECTION_SETUP;
    }

//        @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping(value = "save-with-vacancy")
    public ResponseEntity<?> saveWithVacancy(@Valid @RequestBody SectionDesignationPojo sectionDesignationPojo
            , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {

            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            sectionDesignationService.saveByVacancy(sectionDesignationPojo))
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody SectionDesignationPojo sectionDesignationPojo
            , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {

            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            sectionDesignationService.save(sectionDesignationPojo))
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@Valid @RequestBody List<SectionDesignationPojo> sectionDesignationPojo
            , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {

            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            sectionDesignationService.saveAll(sectionDesignationPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    @SectionDesignationLogExecution(SectionDesignationActivity.UPDATE_EMPLOYEE_SECTION_DESIGNATION)
    public ResponseEntity<?> update(@Valid @RequestBody SectionDesignationPojo sectionDesignationPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {

            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            sectionDesignationService.update(sectionDesignationPojo))
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        SectionDesignationPojo s = sectionDesignationService.findSectionDesignationById(id);
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

//    @PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping(value = "/section")
    public ResponseEntity<?> getParentOnlySectionDesignation(@PathVariable Integer id) {
        SectionDesignationPojo s = sectionDesignationService.findSectionDesignationById(id);
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

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
            sectionDesignationService.deleteDesignation(id);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            null));
    }
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @PutMapping("/remove-employee/{id}")
    @SectionDesignationLogExecution(SectionDesignationActivity.REMOVE_EMPLOYEE_SECTION_DESIGNATION)
    public ResponseEntity<?> unAssignEmployee(@PathVariable("id") Integer id) {

        if (sectionDesignationService.detachEmployee(id) != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping("/assign-employee")
    @SectionDesignationLogExecution(SectionDesignationActivity.ASSIGN_EMPLOYEE_SECTION_DESIGNATION)
    public ResponseEntity<?> assignEmployee(@RequestBody SectionDesignationPojo sectionDesignationPojo) {

        if (sectionDesignationService.assignEmployee(sectionDesignationPojo) != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionReport,'update')")
    @PostMapping("/activate-section")
    public ResponseEntity<?> changeActiveSection(@RequestParam("sectionDesignationId") Integer sectionDesignationId) {
        if (Boolean.TRUE.equals(sectionDesignationService.changeActiveSectionDesignation(sectionDesignationId))) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            "Successfully updated")
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

//    @PutMapping("/assign-office-head")
//    public ResponseEntity<?> assignOfficeHead(@RequestBody RolePojo rolePojo) {
//
//        if (sectionDesignationService.assignEmployee(sectionDesignationPojo) != null) {
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
//                            null)
//            );
//        } else {
//            return ResponseEntity.ok(
//                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
//                            null)
//            );
//        }
//    }
//
//    @PutMapping("/assign-section-head")
//    public ResponseEntity<?> assignSectionHead(@RequestBody  ) {
//
//        if (sectionDesignationService.assignEmployee(sectionDesignationPojo) != null) {
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
//                            null)
//            );
//        } else {
//            return ResponseEntity.ok(
//                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
//                            null)
//            );
//        }
//    }

//    @PreAuthorize("hasPermission(#this.this.permissionReport,'read')")
    @GetMapping(value = "/all-by-employee")
    public ResponseEntity<?> geSectionDesignationByEmployee(@RequestParam String pisCode) {
        List<SectionDesignationPojo> s = sectionDesignationService.findSectionDesignationByEmployee(pisCode);
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
