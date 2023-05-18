//package com.gerp.attendance.controller.Setup;
//
//import com.gerp.attendance.Pojo.DistrictPojo;
//import com.gerp.attendance.Pojo.EmployeeAttendancePojo;
//import com.gerp.attendance.Pojo.PeriodicHolidayPojo;
//import com.gerp.attendance.Pojo.SetupPojo;
//import com.gerp.attendance.constant.PermissionConstants;
//import com.gerp.attendance.model.attendances.EmployeeAttendance;
//import com.gerp.attendance.model.setup.District;
//import com.gerp.attendance.model.setup.PeriodicHoliday;
//import com.gerp.attendance.service.DistrictService;
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
//@RequestMapping("/district")
//public class DistrictController extends GenericCrudController<District, Long> {
//    private final DistrictService districtService;
//    private final CustomMessageSource customMessageSource;
//
//    public DistrictController(DistrictService districtService, CustomMessageSource customMessageSource) {
//        this.districtService = districtService;
//        this.customMessageSource = customMessageSource;
//        this.moduleName = PermissionConstants.DISTRICT_MODULE_NAME;
//        this.permissionName = PermissionConstants.MASTERDATA + "_" + PermissionConstants.DISTRICT_SETUP;
//    }
//
//    @GetMapping(value = "find-by-province-id/{id}")
//    public ResponseEntity<?> getDistrictByProvinceId(@PathVariable Long id) {
//
//        ArrayList<SetupPojo> districts = districtService.getByProvinceId(id);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        districts)
//        );
//
//    }
//
////    @GetMapping(value = "findAll")
////    public ResponseEntity<?> getAllDistrict() {
////        ArrayList<SetupPojo> districts = districtService.getAllDistrict();
////        return ResponseEntity.ok(
////                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
////                        districts)
////        );
////
////    }
//
//    @PostMapping(value="/save")
//    public ResponseEntity<?> create(@Valid @RequestBody DistrictPojo districtPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            District district= districtService.save(districtPojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
//                            district.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<?> update(@Valid @RequestBody DistrictPojo districtPojo, BindingResult bindingResult) throws BindException {
//
//        if (!bindingResult.hasErrors()) {
//            District district = districtService.update(districtPojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
//                            district.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//}
