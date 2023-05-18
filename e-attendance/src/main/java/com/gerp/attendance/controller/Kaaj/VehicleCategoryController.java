package com.gerp.attendance.controller.Kaaj;

import com.gerp.attendance.Pojo.VehicleCategoryPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.kaaj.VehicleCategory;
import com.gerp.attendance.service.VehicleCategoryService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/vehicle-category")
public class VehicleCategoryController extends GenericCrudController<VehicleCategory, Integer> {

    private final VehicleCategoryService vehicleCategoryService;
    private final CustomMessageSource customMessageSource;

    public VehicleCategoryController(VehicleCategoryService vehicleCategoryService, CustomMessageSource customMessageSource) {
        this.vehicleCategoryService = vehicleCategoryService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.VEHICLE_CATEGORY_MODULE_NAME;
        this.permissionName= PermissionConstants.VEHICLE_CATEGORY+"_"+PermissionConstants.VEHICLE_CATEGORY_SETUP;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllVendor()  {

        ArrayList<VehicleCategoryPojo> vehicleCategories = vehicleCategoryService.getAllVehicle();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        vehicleCategories)
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
//    @DeleteMapping("/delete/{vehicleId}")
    public ResponseEntity<?> deleteVehicleCategory(@PathVariable("vehicleId") Integer vehicleId) {
        VehicleCategory vehicleCategory=vehicleCategoryService.findById(vehicleId);
        if(vehicleCategory!=null) {
            vehicleCategoryService.deleteVehicle(vehicleId);
            VehicleCategory vehicleCategoryDetail = vehicleCategoryService.findById(vehicleId);
            if (vehicleCategoryDetail.getActive()) {
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName.toLowerCase())),
                                vehicleId)
                );
            } else {
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.inactive", customMessageSource.get(moduleName.toLowerCase())),
                                vehicleId)
                );
            }
        }else{
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }

    }



}
