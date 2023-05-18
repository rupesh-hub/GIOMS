package com.gerp.attendance.controller.holiday;

import com.gerp.attendance.Pojo.HolidayMapperPojo;
import com.gerp.attendance.Pojo.PublicHolidaySetupPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.setup.PublicHolidaySetup;
import com.gerp.attendance.service.PublicHolidaySetupService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/holiday-setup")
public class PublicHolidaySetupController extends BaseController {

    private final PublicHolidaySetupService publicHolidaySetupService;
    private final CustomMessageSource customMessageSource;

    public PublicHolidaySetupController(PublicHolidaySetupService publicHolidaySetupService, CustomMessageSource customMessageSource) {
        this.publicHolidaySetupService = publicHolidaySetupService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.PUBLIC_HOLIDAY_MODULE_NAME;
        this.permissionName= PermissionConstants.HOLIDAY +"_"+PermissionConstants.PUBLIC_HOLIDAY_SETUP;
    }


    /**
     * This method adds public holiday
     * @param publicHolidaySetupPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PublicHolidaySetupPojo publicHolidaySetupPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            PublicHolidaySetup publicHolidaySetup= publicHolidaySetupService.save(publicHolidaySetupPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            publicHolidaySetup.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Gets Public Holiday by id
     * @param id
     * @return
     */
    @GetMapping(value="/{id}")
    public ResponseEntity<?> getPublicHolidayById(@PathVariable Integer  id)  {
        HolidayMapperPojo holidayMapperPojo = publicHolidaySetupService.getHolidayById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidayMapperPojo)
        );
    }


    /**
     * This method updates the specific public holiday
     * @param publicHolidaySetupPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody PublicHolidaySetupPojo publicHolidaySetupPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            publicHolidaySetupService.updatePublicHoliday(publicHolidaySetupPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            publicHolidaySetupPojo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Gets All Public Holiday
     * @return
     */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        ArrayList<HolidayMapperPojo> holidayMapperPojo = publicHolidaySetupService.getAllHolidays();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidayMapperPojo)
        );
    }

    /**
     * Gets Public Holiday officeWise
     * @param officeCode
     * @return
     */
    @GetMapping("/get-by-office/{officeCode}")
    public ResponseEntity<?> getByOfficeCode(@PathVariable("officeCode") String officeCode) {
        ArrayList<HolidayMapperPojo> holidayMapperPojo = publicHolidaySetupService.getByOfficeCode(officeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidayMapperPojo)
        );
    }

    /**
     * Gets Public Holiday for specific gender
     * @param holidayFor
     * @return
     */
    @GetMapping("/get-by-gender/{gender}")
    public ResponseEntity<?> getHolidayFor(@PathVariable("gender") String holidayFor) {
        ArrayList<HolidayMapperPojo> holidayMapperPojo = publicHolidaySetupService.getHolidayFor(holidayFor);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidayMapperPojo)
        );
    }

    /**
     * Soft Delete Specific holiday
     * @param holidayId
     * @return
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value = "/{holidayId}")
    public ResponseEntity<?> softDeleteHoliday(@PathVariable("holidayId") Integer holidayId) {
        publicHolidaySetupService.softDeleteHoliday(holidayId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.status", customMessageSource.get(moduleName.toLowerCase())),
                        holidayId)
        );
    }

    /**
     * Hard Delete Specific holiday
     * @param holidayId
     * @return
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value = "/hard-delete/{holidayId}")
    public ResponseEntity<?> hardDeleteHoliday(@PathVariable("holidayId") Integer holidayId) {
        publicHolidaySetupService.hardDelete(holidayId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        holidayId)
        );
    }
}
