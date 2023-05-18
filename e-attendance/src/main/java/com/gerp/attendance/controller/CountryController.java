//package com.gerp.attendance.controller;
//
//import com.gerp.attendance.constant.PermissionConstants;
//import com.gerp.attendance.model.setup.Country;
//import com.gerp.attendance.service.CountryService;
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.generic.controllers.generic.GenericCrudController;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@RestController
//@RequestMapping("/country")
//public class CountryController extends GenericCrudController<Country, Long> {
//    private final CountryService countryService;
//    private final CustomMessageSource customMessageSource;
//
//    public CountryController(CountryService countryService, CustomMessageSource customMessageSource) {
//        this.countryService = countryService;
//        this.customMessageSource = customMessageSource;
//        this.moduleName= PermissionConstants.COUNTRY_MODULE_NAME;
//        this.permissionName= PermissionConstants.MASTERDATA+"_"+PermissionConstants.COUNTRY_SETUP;
//    }
//}
