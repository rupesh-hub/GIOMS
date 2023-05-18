package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeSavePojo;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/office")
public class OfficeController extends BaseController {
    private final OfficeService officeService;
    private final CustomMessageSource customMessageSource;


    public OfficeController(OfficeService officeService, CustomMessageSource customMessageSource) {
        this.officeService = officeService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.OFFICE;
    }

    @GetMapping(value = "/officeByCode")
    public ResponseEntity<?> getOfficeBelowHierarchy(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.findByCode(officeCode)));
    }
    @GetMapping(value = "/detail")
    public ResponseEntity<?> getOfficeDetail(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.officeDetail(officeCode)));
    }
    @GetMapping(value = "/minimal-detail")
    public ResponseEntity<?> getMinimalOfficeDetail(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.officeMinimalDetail(officeCode)));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllTopOffice() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.getAllTopParentOffice()));
    }

    @GetMapping(value = "/all-office")
    public ResponseEntity<?> getAllOffice( @RequestParam(required = false,defaultValue = "10") int limit,
                                           @RequestParam(required = false,defaultValue = "1") int page,
                                           @RequestParam(required = false) String searchKey,
                                           @RequestParam(required = false) String districtCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.allOffices(limit,page,searchKey,districtCode)));
    }

    @GetMapping(value = "/office-section-by-code")
    public ResponseEntity<?> getAllOfficeWithSection(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.officeSectionListByCode(officeCode)));
    }

    @GetMapping(value = "/office-lower-hierarchy")
    public ResponseEntity<?> getChildHierarchy(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.officeLowerHierarchyList(officeCode)));
    }

    @GetMapping(value = "/office-higher-hierarchy-list")
    public ResponseEntity<?> getHigherHierarchyLists(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.officeHigherHierarchyListOnly(officeCode)));
    }

    @GetMapping(value = "/top-level-office")
    public ResponseEntity<?> getHighestOffice(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.officeHigherHierarchyListOnly(officeCode)));
    }

    @GetMapping(value = "/child-offices")
    public ResponseEntity<?> getSubOffice(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.getChildOffice(officeCode)));
    }

    @GetMapping(value = "/parent-offices")
    public ResponseEntity<?> getParentOffice(@RequestParam String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.getParentOffices(officeCode)));
    }

    @PostMapping(value = "/office-search")
    public ResponseEntity<?> getOfficeByFilterParam(@RequestBody SearchPojo search) {
        List<OfficePojo> offices =  officeService.officeListByParams(search);
        if (offices != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            offices)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }
    @GetMapping(value = "/gioms-active-office")
    public ResponseEntity<?> getGIOMSActiveOfficeList() {
        List<OfficePojo> offices =  officeService.getGiomsActiveOfficeList();
        if (offices != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            offices)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping(value = "/ministry-offices")
    public ResponseEntity<?> getMinistryOffices() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.getMinistryOffices()));
    }

    @PostMapping(value = "/office-status")
    public ResponseEntity<?> getOfficeActiveStatus(@RequestBody List<String> officeCodes) {
        if(officeCodes.isEmpty()){
            throw new RuntimeException("officeCodes Cannot be empty");
        }
        List<OfficePojo> offices =  officeService.officeActiveStatus(officeCodes);
        if (offices != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            offices)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveOffice(@RequestBody OfficeSavePojo officePojo) {
        String code =  officeService.saveOffice(officePojo);
        if (code != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.save", customMessageSource.get(moduleName.toLowerCase())),
                            code)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> updateOffice(@RequestBody OfficeSavePojo officePojo) {
        String officeCode =  officeService.updateOffice(officePojo);
        if (officeCode != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            officeCode)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }


    @PostMapping(value = "/all-office-paginated")
    public ResponseEntity<?> getAllOfficePaginated(@RequestBody GetRowsRequest paginatedRequest) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.allOfficePaginated(paginatedRequest)));
    }


    @PutMapping(value = "/activate-office/{code}")
    public ResponseEntity<?> activateOffice(@PathVariable("code") String code) {
        boolean isActivated = officeService.activateOffice(code);
        if (isActivated) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            true)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping(value = "/lower-office-employee")
    public ResponseEntity<?> getAllOfficePaginated(@RequestParam("office")String office) {
        Map<String,List<String>> employees=new HashMap<>();
         employees.put("employeeList", officeService.getLowerOfficeEmployee(office));
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        employees));
    }

    @PutMapping(value = "/update-setup-status")
    public ResponseEntity<?> updateSetUpStatus() {
        officeService.updateSetupStatus();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.update", customMessageSource.get(moduleName.toLowerCase())),
                        null));
    }

    @GetMapping(value = "/set-up-status")
    public ResponseEntity<?> getSetUpStatus() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                        officeService.getSetUpStatus()));
    }
}
