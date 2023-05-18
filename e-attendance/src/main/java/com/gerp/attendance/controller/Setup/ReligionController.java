//package com.gerp.attendance.controller.Setup;
//
//import com.gerp.attendance.constant.PermissionConstants;
//import com.gerp.attendance.model.setup.Religion;
//import com.gerp.attendance.service.ReligionService;
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
//@RequestMapping("/religion")
//public class ReligionController extends GenericCrudController<Religion, Integer> {
//    private final ReligionService religionService;
//    private final CustomMessageSource customMessageSource;
//
//    public ReligionController(ReligionService religionService, CustomMessageSource customMessageSource) {
//        this.religionService = religionService;
//        this.customMessageSource = customMessageSource;
//        this.moduleName= PermissionConstants.RELIGION_MODULE_NAME;
//        this.permissionName= PermissionConstants.HOLIDAY +"_"+PermissionConstants.RELIGION_SETUP;
//    }
//
//}
