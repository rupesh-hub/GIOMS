package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.address.MunicipalityVdc;
import com.gerp.usermgmt.services.organization.Location.MunicipalityVdcService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/municipality-vdc")
public class MunicipalityVdcController extends GenericCrudController<MunicipalityVdc, String> {
    private final CustomMessageSource customMessageSource;
    private final MunicipalityVdcService municipalityVdcService;

    public MunicipalityVdcController(CustomMessageSource customMessageSource, MunicipalityVdcService municipalityVdcService) {
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.ADDRESS_SETUP;
        this.municipalityVdcService = municipalityVdcService;
    }


    @GetMapping("/get-by-district")
    public ResponseEntity<?> getFilteredDesignation(@RequestParam("districtCode") String districtCode) {
        List<IdNamePojo> s = municipalityVdcService.municiplityByDistrict(districtCode);
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
