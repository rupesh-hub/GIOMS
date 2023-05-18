package com.gerp.usermgmt.controller.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.FunctionalDesignationPojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import com.gerp.usermgmt.services.organization.designation.FunctionalDesignationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/designation")
@Slf4j
public class FunctionalDesignationController extends GenericCrudController<FunctionalDesignation, String> {
    private final CustomMessageSource customMessageSource;
    private final FunctionalDesignationService designationService;

    public FunctionalDesignationController(CustomMessageSource customMessageSource, FunctionalDesignationService designationService) {
        this.customMessageSource = customMessageSource;
        this.designationService = designationService;
        this.moduleName = PermissionConstants.DESIGNATION_CLASS;
    }


//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@Valid @RequestBody List<FunctionalDesignation> designations, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                designationService.saveMany(designations);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                                null));
            }catch (Exception e){
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }

    @PostMapping(value = "save")
    public ResponseEntity<?> create(@Valid @RequestBody FunctionalDesignationPojo functionalDesignation, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                String t = designationService.save(functionalDesignation);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                                t)
                );
            }catch (Exception e){
                log.error(e.getMessage());
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }
    @PutMapping(value = "update")
    public ResponseEntity<?> update(@Valid @RequestBody FunctionalDesignationPojo functionalDesignation, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                String t = designationService.update(functionalDesignation);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                                t)
                );
            }catch (Exception e){
                log.error(e.getMessage());
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }


    @PostMapping("/get-all")
    public ResponseEntity<?> getFilteredDesignation(@RequestBody SearchPojo searchPojo) {
        List<IdNamePojo> s = designationService.designationSearch(searchPojo);
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

    @PostMapping("/office-employee-designations")
    public ResponseEntity<?> getOfficeDesignation(@RequestBody SearchPojo searchPojo) {
        List<IdNamePojo> s = designationService.officeDesignationSearch(searchPojo);
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

    @GetMapping(value = "/office-designations")
    public ResponseEntity<?> getOfficeDesignaion(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        designationService.officeDesignationList(officeCode)));
    }

//    @GetMapping(value = "/office-designations")
//    public ResponseEntity<?> designationDetail(@RequestParam String officeCode) {
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
//                        designationService.officeDesignationList(officeCode)));
//    }


    @GetMapping(value = "/designation-by-section")
    public ResponseEntity<?> getDesignationBySection(@RequestParam("sectionId") Long id) {
        List<IdNamePojo> s = designationService.findSectionDesignationById(id);
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

    @Override
    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable String id) throws Exception {
        FunctionalDesignation t = designationService.findById(id);
        if (t != null) {
            designationService.deleteDesignation(id);
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

    @Override
    @PostMapping(value = "paginated-list")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<FunctionalDesignationPojo> page = designationService.getFilterPaginated(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @GetMapping(value = "/by-code/{code}")
    public ResponseEntity<?> getDesignationDetailById(@PathVariable String code) throws Exception {
        DetailPojo t = designationService.getDesignationByCode(code);
        if (t != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            t)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }
}
