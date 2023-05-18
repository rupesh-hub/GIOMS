//package com.gerp.attendance.controller.Setup;
//
//import com.gerp.attendance.Pojo.DistrictPojo;
//import com.gerp.attendance.Pojo.LocationPojo;
//import com.gerp.attendance.Pojo.SetupPojo;
//import com.gerp.attendance.constant.PermissionConstants;
//import com.gerp.attendance.model.setup.District;
//import com.gerp.attendance.model.setup.Location;
//import com.gerp.attendance.service.LocationService;
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.generic.controllers.generic.GenericCrudController;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindException;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.ArrayList;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@RestController
//@RequestMapping("/location")
//public class LocationController extends GenericCrudController<Location, Long> {
//    private final LocationService locationService;
//    private final CustomMessageSource customMessageSource;
//
//    public LocationController(LocationService locationService, CustomMessageSource customMessageSource) {
//        this.locationService = locationService;
//        this.customMessageSource = customMessageSource;
//        this.moduleName = PermissionConstants.LOCATION_MODULE_NAME;
//        this.permissionName = PermissionConstants.MASTERDATA + "_" + PermissionConstants.LOCATION_SETUP;
//    }
//
//    @GetMapping(value = "find-by-district-id/{id}")
//    public ResponseEntity<?> getByDistrictId(@PathVariable Long id) {
//
//        ArrayList<SetupPojo> locations = locationService.getByDistrictId(id);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        locations)
//        );
//
//    }
//
//    @PostMapping(value="/save")
//    public ResponseEntity<?> create(@Valid @RequestBody LocationPojo locationPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            Location location= locationService.save(locationPojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
//                            location.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<?> update(@Valid @RequestBody LocationPojo locationPojo, BindingResult bindingResult) throws BindException {
//
//        if (!bindingResult.hasErrors()) {
//            Location location = locationService.update(locationPojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
//                            location.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//}
