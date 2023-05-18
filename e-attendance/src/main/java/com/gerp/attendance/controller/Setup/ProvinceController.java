//package com.gerp.attendance.controller.Setup;
//
//import com.gerp.attendance.Pojo.DistrictPojo;
//import com.gerp.attendance.Pojo.LocationPojo;
//import com.gerp.attendance.Pojo.ProvincePojo;
//import com.gerp.attendance.Pojo.SetupPojo;
//import com.gerp.attendance.constant.PermissionConstants;
//import com.gerp.attendance.model.setup.District;
//import com.gerp.attendance.model.setup.Location;
//import com.gerp.attendance.model.setup.Province;
//import com.gerp.attendance.service.ProvinceService;
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
//@RequestMapping("/province")
//public class ProvinceController extends GenericCrudController<Province, Long> {
//    private final ProvinceService provinceService;
//    private final CustomMessageSource customMessageSource;
//
//    public ProvinceController(ProvinceService provinceService, CustomMessageSource customMessageSource) {
//        this.provinceService = provinceService;
//        this.customMessageSource = customMessageSource;
//        this.moduleName = PermissionConstants.PROVINCE_MODULE_NAME;
//        this.permissionName = PermissionConstants.MASTERDATA + "_" + PermissionConstants.PROVINCE_SETUP;
//    }
//
//    @GetMapping(value="find-by-country-id/{id}")
//    public ResponseEntity<?> getAttendanceDevice(@PathVariable Long  id)  {
//
//        ArrayList<SetupPojo> provinces= provinceService.getByCountryId(id);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        provinces)
//        );
//
//    }
//
//    @PostMapping(value="/save")
//    public ResponseEntity<?> create(@Valid @RequestBody ProvincePojo provincePojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            Province province= provinceService.save(provincePojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
//                            province.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<?> update(@Valid @RequestBody ProvincePojo provincePojo, BindingResult bindingResult) throws BindException {
//
//        if (!bindingResult.hasErrors()) {
//            Province province = provinceService.update(provincePojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
//                            province.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//}
