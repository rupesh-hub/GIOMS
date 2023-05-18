package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.converter.organiztion.office.OrganisationTypeConverter;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.pojo.organization.office.OrganisationTypePojo;
import com.gerp.usermgmt.services.organization.office.OfficeTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/office-type")
public class OfficeTypeController extends GenericCrudController<OrganisationType, Long> {
    private final OfficeTypeService officeTypeService;
    private final OrganisationTypeConverter organisationTypeConverter;

    public OfficeTypeController(CustomMessageSource customMessageSource, OfficeTypeService officeTypeService, OrganisationTypeConverter organisationTypeConverter) {
        this.organisationTypeConverter = organisationTypeConverter;
        this.customMessageSource = customMessageSource;
        this.officeTypeService = officeTypeService;
        this.moduleName = PermissionConstants.OFFICE_TYPE;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllOfficeLevel() {
        List<IdNamePojo> s = officeTypeService.findAll();
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

        @PostMapping(value = "save")
        public ResponseEntity<?> create(@Valid @RequestBody OrganisationTypePojo orgPojo, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
            if (!bindingResult.hasErrors()) {
                try {
                    OrganisationType t = officeTypeService.create(organisationTypeConverter.toEntity(orgPojo));
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                                    t.getId())
                    );
                }catch (Exception e){
                    return ResponseEntity.ok(
                            errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                    );
                }
            } else {

                throw new BindException(bindingResult);

            }
        }

        @PutMapping(value = "update")
        public ResponseEntity<?> update(@Valid @RequestBody OrganisationTypePojo orgPojo) throws BindException {
//        DayType.getKey()
            OrganisationType org = officeTypeService.findById(orgPojo.getId());
            if( org == null) {
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("crud.not_exits"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
                try {
                    OrganisationType t = officeTypeService.create(organisationTypeConverter.toUpdateEntity(orgPojo , org));
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                                    t.getId())
                    );
                }catch (Exception e){
                    return ResponseEntity.ok(
                            errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                    );
                }

        }

    @DeleteMapping("/inactive/{id}")
    public ResponseEntity<?> deActive(@PathVariable("id") Long id) {
        try {
            officeTypeService.deActiveOfficeType(id);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            null));
        }catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }


}
