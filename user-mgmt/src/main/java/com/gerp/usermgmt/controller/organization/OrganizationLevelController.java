package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.converter.organiztion.office.OrganisationLevelConverter;
import com.gerp.usermgmt.model.office.OrganizationLevel;
import com.gerp.usermgmt.pojo.organization.office.OrganizationLevelPojo;
import com.gerp.usermgmt.services.organization.office.OfficeLevelService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/office-level")
public class OrganizationLevelController extends BaseController {
    private final CustomMessageSource customMessageSource;
    private final OfficeLevelService officeLevelService;
    private final OrganisationLevelConverter organisationLevelConverter;

    public OrganizationLevelController(CustomMessageSource customMessageSource, OfficeLevelService officeLevelService, OrganisationLevelConverter organisationLevelConverter) {
        this.customMessageSource = customMessageSource;
        this.officeLevelService = officeLevelService;
        this.organisationLevelConverter = organisationLevelConverter;
        this.moduleName = PermissionConstants.OFFICE_LEVEL;
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllOfficeLevel() {
        List<OrganizationLevelPojo> s = officeLevelService.findAll();
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

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody OrganizationLevelPojo organizationLevelPojo) {
        OrganizationLevel organizationLevel = officeLevelService.create(organisationLevelConverter.toEntity(organizationLevelPojo));
        if (organizationLevel != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            organisationLevelConverter.toDto(organizationLevel))
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody OrganizationLevelPojo organizationLevelPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {

            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            organisationLevelConverter.toDto(officeLevelService.update(organisationLevelConverter.toEntity(organizationLevelPojo))))
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) throws Exception {
        OrganizationLevel t = officeLevelService.findById(id);
        if (t != null) {
            officeLevelService.deleteById(id);
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

    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<OrganizationLevel> page = officeLevelService.getPaginated(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        OrganizationLevel organizationLevel = officeLevelService.findById(id);
        if (organizationLevel != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            organisationLevelConverter.toDto(organizationLevel))
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }

    }

}
