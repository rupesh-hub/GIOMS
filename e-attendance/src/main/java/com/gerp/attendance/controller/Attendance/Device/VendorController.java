package com.gerp.attendance.controller.Attendance.Device;

import com.gerp.attendance.Pojo.VendorRequestPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.device.DeviceVendor;
import com.gerp.attendance.service.VendorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/vendor")
public class VendorController extends GenericCrudController<DeviceVendor, Integer> {


    private final VendorService vendorService;
    private final CustomMessageSource customMessageSource;

    public VendorController(VendorService vendorService, CustomMessageSource customMessageSource) {
        this.vendorService = vendorService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.VENDOR_MODULE_NAME;
        this.permissionName = PermissionConstants.ATTENDANCE + "_" + PermissionConstants.VENDOR;
    }

    /**
     * This method adds device vendor
     * @param vendorRequestPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/save")
    public ResponseEntity<?> saveVendor(@Valid @RequestBody VendorRequestPojo vendorRequestPojo, BindingResult bindingResult) throws BindException {

        if(!bindingResult.hasErrors()) {
            DeviceVendor deviceVendor = vendorService.saveVendor(vendorRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            deviceVendor.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }

    }

    /**
     * This method update vendor device
     * @param vendorRequestPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping(value="/update")
    public ResponseEntity<?> updateVendor(@Valid @RequestBody VendorRequestPojo vendorRequestPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            vendorService.updateVendor(vendorRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            vendorRequestPojo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * This method gets all vendor
     * @return
     */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllVendor()  {

        ArrayList<VendorRequestPojo> vendorRequestPojos = vendorService.getAllVendors();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        vendorRequestPojos)
        );
    }

    /**
     * This method gets all active vendor
     * @return
     */
    @GetMapping("/get-all-active")
    public ResponseEntity<?> getAllActiveVendor()  {

        ArrayList<VendorRequestPojo> vendorRequestPojos = vendorService.getAllActiveVendors();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        vendorRequestPojos)
        );
    }

    /**
     * This method soft delete the respective vendor
     * @param vendorId
     * @return
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/delete/{vendorId}")
    public ResponseEntity<?> deleteVendor(@PathVariable("vendorId") Integer vendorId) {
        vendorService.deleteVendor(vendorId);
        DeviceVendor deviceVendor=vendorService.findById(vendorId);
        if(deviceVendor.getActive()){
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName.toLowerCase())),
                            vendorId)
            );
        }else{
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.inactive", customMessageSource.get(moduleName.toLowerCase())),
                            vendorId)
            );
        }

    }
}
