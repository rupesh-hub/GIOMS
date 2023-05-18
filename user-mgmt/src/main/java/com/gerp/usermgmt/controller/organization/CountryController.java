package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.address.Country;
import com.gerp.usermgmt.services.organization.Location.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/country")
public class CountryController extends GenericCrudController<Country, String> {
    private final CountryService countryService;
    private final CustomMessageSource customMessageSource;

    public CountryController(CountryService countryService, CustomMessageSource customMessageSource) {
        this.countryService = countryService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.Locations;
    }


    @GetMapping(value = "/all")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                        countryService.findAllActive())
        );

    }

}
