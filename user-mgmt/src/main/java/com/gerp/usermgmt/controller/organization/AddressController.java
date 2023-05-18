package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.address.Address;
import com.gerp.usermgmt.services.organization.Address.AddressService;
import com.gerp.usermgmt.services.organization.Location.DistrictService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
public class AddressController extends GenericCrudController<Address, Long> {
    private final AddressService addressService;
    private final CustomMessageSource customMessageSource;
    private final DistrictService districtService;

    public AddressController(AddressService addressService, CustomMessageSource customMessageSource, DistrictService districtService) {
        this.addressService = addressService;
        this.districtService = districtService;
        this.moduleName = PermissionConstants.ADDRESS_SETUP;
        this.customMessageSource = customMessageSource;
    }
}
