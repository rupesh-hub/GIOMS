package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.employee.SectionSubsection;
import com.gerp.usermgmt.pojo.organization.office.SectionPojo;
import com.gerp.usermgmt.services.organization.office.SectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/section")
@Slf4j
public class SectionController extends BaseController {

    private final CustomMessageSource customMessageSource;
    private final SectionService sectionService;

    public SectionController(CustomMessageSource customMessageSource, SectionService sectionService) {
        this.customMessageSource = customMessageSource;
        this.sectionService = sectionService;
        this.moduleName = PermissionConstants.SECTION;
    }

    @GetMapping(value = "/section-list-hierarchy")
    public ResponseEntity<?> getOfficeBelowHierarchy(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        sectionService.getSectionSubsectionOfOffice(officeCode)));
    }

    @GetMapping(value = "/section-list")
    public ResponseEntity<?> getSectionOfOffice() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        sectionService.getSectionListByLoggedOffice()));
    }
    @GetMapping(value = "/section-list-by-employee")
    public ResponseEntity<?> getSectionListByEmployee(@RequestParam("pisCode")String pisCode) {
        List<SectionPojo> sectionPojos =  sectionService.getSectionListByEmployee(pisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                       sectionPojos));
    }

    @GetMapping(value = "/sectionListByOffice")
    public ResponseEntity<?> getSectionOfOffice(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        sectionService.getSectionListByOffice(officeCode)));
    }

    @GetMapping(value = "/section-with-darbandi")
    public ResponseEntity<?> getSectionDarbandiOfOffice(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        sectionService.getSectionSubsectionWithDarbandi(officeCode)));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getSectionByCode(@PathVariable("id")  Long id) {
        log.info("getting section detail, URI: /section/{id}");
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        sectionService.getSectionSubsectionById(id)));
    }
    @GetMapping(value = "/subsection")
    public ResponseEntity<?> getSub(@RequestParam Long id) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        sectionService.getSubSection(id)));
    }

    @GetMapping(value = "/parentSectionOfOffice")
    public ResponseEntity<?> getParentSectionOfOffice(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        sectionService.getParentSectionOfOffice(officeCode)));
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody SectionPojo sectionPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = sectionService.addSection(sectionPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            id)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody SectionPojo sectionPojo, BindingResult bindingResult) throws BindException {
        try {
            if (!bindingResult.hasErrors()) {

                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                                sectionService.update(sectionPojo))
                );
            } else {
                throw new BindException(bindingResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("cant.update"), customMessageSource.get(moduleName.toLowerCase()))
            );
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        SectionSubsection sectionSubsection = sectionService.findById(id);
        if (sectionSubsection != null) {
            sectionService.deleteById(id);
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

    @GetMapping(value = "/sectionDarbandiByOffice")
    public ResponseEntity<?> getSectionDesignationParents(@RequestParam String officeCode) {
        List<SectionPojo> sections = sectionService.getSectionSubsectionWithDarbandiParents(officeCode);
        if (sections != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            sections)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping(value = "/subsectionDarbandiBySection")
    public ResponseEntity<?> getSectionDesignationByParent(@RequestParam Long sectionId) {
        List<SectionPojo> sections = sectionService.getSubsectionWithDarbandiByParents(sectionId);
        if (sections != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            sections)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping(value = "/all-sections-subsections-by-employee")
    public ResponseEntity<?> getSectionSubsectionsByEmployee(@RequestParam Long sectionId) {
        List<SectionPojo> sections = sectionService.getSubsectionWithDarbandiByParents(sectionId);
        if (sections != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            sections)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }
}
