package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.services.organization.Location.DistrictService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/district")
public class DistrictController extends BaseController {
    private final CustomMessageSource customMessageSource;
    private final DistrictService districtService;

    public DistrictController(CustomMessageSource customMessageSource, DistrictService districtService) {
        this.customMessageSource = customMessageSource;
        this.districtService = districtService;
        this.moduleName = PermissionConstants.OFFICE_LEVEL;
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/get-all")
    public ResponseEntity<?> getFilteredDesignation() {
        List<IdNamePojo> s = districtService.findAllDistricts();
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

    //@PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping(value = "{code}")
    public ResponseEntity<?> get(@PathVariable String code) {
        IdNamePojo t = districtService.findByIdMinimal(code);
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
