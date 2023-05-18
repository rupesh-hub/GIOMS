package com.gerp.attendance.controller.holiday;

import com.gerp.attendance.Pojo.DateCountPojo;
import com.gerp.attendance.Pojo.HolidayResponsePojo;
import com.gerp.attendance.Pojo.PeriodicHolidayPojo;
import com.gerp.attendance.Pojo.PeriodicHolidayRequestPojo;
import com.gerp.attendance.Pojo.holiday.HolidayCountDetailPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.setup.PeriodicHoliday;
import com.gerp.attendance.service.PeriodicHolidayService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/periodic-holiday")
public class PeriodicHolidayController extends BaseController {
    private final PeriodicHolidayService periodicHolidayService;
    private final CustomMessageSource customMessageSource;
    @Autowired
    private TokenProcessorService tokenProcessorService;

    public PeriodicHolidayController(PeriodicHolidayService periodicHolidayService, CustomMessageSource customMessageSource) {
        this.periodicHolidayService = periodicHolidayService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.PERIODIC_HOLIDAY_MODULE_NAME;
        this.permissionName= PermissionConstants.HOLIDAY +"_"+PermissionConstants.PERIODIC_HOLIDAY_SETUP;
    }

    /**
     * This method adds periodic holiday
     * @param periodicHolidayPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PeriodicHolidayRequestPojo periodicHolidayPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            List<PeriodicHoliday> periodicHoliday= periodicHolidayService.save(periodicHolidayPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            periodicHoliday)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * This method updates the periodic holiday
     * @param periodicHolidayPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody PeriodicHolidayPojo periodicHolidayPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            periodicHolidayService.update(periodicHolidayPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            periodicHolidayPojo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/save")
    public ResponseEntity<?> createSingle(@Valid @RequestBody PeriodicHolidayPojo periodicHolidayPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            periodicHolidayService.createSingle(periodicHolidayPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            periodicHolidayPojo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Gets all periodic holiday filter by officeCode
     * @return
     */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        ArrayList<HolidayResponsePojo> holidayResponsePojos = periodicHolidayService.getAllPeriodicHoliday();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidayResponsePojos)
        );
    }

        @PostMapping("/get-all-by-year")
        public ResponseEntity<?> getAll(@RequestParam(name = "year")String year) {
            ArrayList<HolidayResponsePojo> holidayResponsePojos = periodicHolidayService.getAllPeriodicHolidayByYear(year);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            holidayResponsePojos)
            );
    }

    /**
     * Gets periodic holiday by fiscal year
     * @param fiscalYear
     * @return
     */
    @GetMapping("/fiscal-year/{fiscalYear}")
    public ResponseEntity<?> getByFiscalYear(@PathVariable("fiscalYear") String fiscalYear) {
        ArrayList<HolidayResponsePojo> holidayByFiscalYear = periodicHolidayService.getByFiscalYear(fiscalYear);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidayByFiscalYear)
        );
    }

    /**
     * Gets number of holiday for each officeCode
     * @return
     */
    @GetMapping("/fiscal-year/count")
    public ResponseEntity<?> getFiscalYearCount() {
        List<DateCountPojo> fiscalYearCount = periodicHolidayService.countDistinctHolidayDates();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        fiscalYearCount)
        );
    }

    /**
     * Filters holiday by date range
     * @param from
     * @param to
     * @return
     */
    @GetMapping("/date")
    public ResponseEntity<?> getHolidayBetween(@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<HolidayResponsePojo> holidays = periodicHolidayService.getHolidayBetween(from, to);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidays)
        );
    }


    // get total count holiday between date range
    @GetMapping("/count-holiday")
    public ResponseEntity<?> getHolidayCount(@RequestParam String pisCode,@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws ParseException {

        if(pisCode.equals("null")){
            pisCode=tokenProcessorService.getPisCode();
        }
        HolidayCountDetailPojo data = periodicHolidayService.getHolidayCount(pisCode,from, to,false);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        data)
        );
    }

    /**
     * This api update for specific holiday
     * @param holidayId
     * @return
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @GetMapping("/toggle-specific/{holidayId}")
    public ResponseEntity<?> toggleSpecificHoliday(@PathVariable("holidayId") Long holidayId) {
        periodicHolidayService.toggleSpecificHoliday(holidayId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.status", customMessageSource.get(moduleName.toLowerCase())),
                        holidayId)
        );
    }

    /**
     * Soft delete for holiday
     * @param holidayId
     * @return
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/soft-delete/{holidayId}")
    public ResponseEntity<?> softDeleteHoliday(@PathVariable("holidayId") Long holidayId) {
        periodicHolidayService.softDeleteHoliday(holidayId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.status", customMessageSource.get(moduleName.toLowerCase())),
                        holidayId)
        );
    }

    /**
     * Gets holiday filter by month and year for specific officeCode
     * @param officeCode
     * @param month
     * @param year
     * @return
     */
    @GetMapping("/year-month")
    public ResponseEntity<?> getHolidayByYearAndMonth(@RequestParam(name = "officeCode") String officeCode, @RequestParam(name = "month") String month,@RequestParam(name = "year") String year){
        List<HolidayResponsePojo> holidays = periodicHolidayService.getHolidayByYearAndMonth(officeCode,month,year);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidays)
        );
    }

    /**
     * Get holiday by date range
     * @param officeCode
     * @param from
     * @param to
     * @return
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getHolidayByYearAndMonth(@RequestParam(name = "officeCode") String officeCode, @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){
        List<HolidayResponsePojo> holidays = periodicHolidayService.getHolidayByDateRange(officeCode,from,to);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        holidays)
        );
    }

    @GetMapping("/inactive-holidays")
    public ResponseEntity<?> inActiveHolidays(@RequestParam(name = "year") String year, @RequestParam(name = "officeCode") String officeCode) {

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        periodicHolidayService.getHolidaysNotSetUp(year, officeCode))
        );

    }

}
