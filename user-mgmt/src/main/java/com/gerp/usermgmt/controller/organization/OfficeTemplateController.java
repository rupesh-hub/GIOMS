package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.converter.organiztion.office.OfficeTemplateConverter;
import com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo;
import com.gerp.usermgmt.services.organization.office.OfficeTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/office-template")
public class OfficeTemplateController extends BaseController {
    private final OfficeTemplateService officeTemplateService;
    private final OfficeTemplateConverter officeTemplateConverter;

    public OfficeTemplateController(OfficeTemplateService officeTemplateService, OfficeTemplateConverter officeTemplateConverter, CustomMessageSource customMessageSource) {
        this.officeTemplateService = officeTemplateService;
        this.officeTemplateConverter = officeTemplateConverter;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.OFFICE_TEMPLATE;
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody OfficeTemplatePojo officeTemplatePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id= officeTemplateService.save(officeTemplateConverter.toEntity(officeTemplatePojo));
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            id)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody OfficeTemplatePojo officeTemplatePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long officeTemplate = officeTemplateService.updateOfficeTemplate(officeTemplatePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            officeTemplate)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }


    @PostMapping("/activate-template")
    public ResponseEntity<?> changeActiveSection(@RequestParam("id") Long officeTemplateId) {
        if (Boolean.TRUE.equals(officeTemplateService.changeActiveTemplate(officeTemplateId))) {
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

    @GetMapping(value = "/get-by-office")
    public ResponseEntity<?> getDesignationBySection(@RequestParam("officeCode") String officeCode, @RequestParam("type") TemplateType type) {
        List<OfficeTemplatePojo> s = officeTemplateService.findByOfficeCode(officeCode, type);
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

    @GetMapping(value = "/get-active-by-office")
    public ResponseEntity<?> getAct(@RequestParam("officeCode") String officeCode,  @RequestParam("type") TemplateType type) {
        OfficeTemplatePojo s = officeTemplateService.findActiveByOfficeCode(officeCode, type);
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


    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        OfficeTemplatePojo s = officeTemplateConverter.toDto(officeTemplateService.findById(id));
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

    /** created different api for default so that we have control over default template office and config**/
    @PostMapping("/save-default")
    public ResponseEntity<?> saveDefault(@Valid @RequestBody OfficeTemplatePojo officeTemplatePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            officeTemplatePojo.setIsDefault(true);
            Long id= officeTemplateService.saveDefault(officeTemplateConverter.toEntity(officeTemplatePojo));
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            id)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping(value = "/default")
    public ResponseEntity<?> getDefaultTemplate(@RequestParam("type") TemplateType type) {
        OfficeTemplatePojo s = officeTemplateService.findDefaultTemplate(type);
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
