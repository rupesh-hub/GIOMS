package com.gerp.attendance.controller.Shift;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Converter.ShiftConverter;
import com.gerp.attendance.Pojo.shift.ShiftDetailPojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.shift.Shift;
import com.gerp.attendance.service.ShiftService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/shift")
public class ShiftController extends BaseController {

    private final ShiftService shiftService;
    private final ShiftConverter shiftConverter;
    private final CustomMessageSource customMessageSource;

    public ShiftController(ShiftService shiftService,ShiftConverter shiftConverter, CustomMessageSource customMessageSource) {
        this.shiftService = shiftService;
        this.shiftConverter=shiftConverter;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.SHIFT_MODULE_NAME;
        this.permissionName= PermissionConstants.SHIFT+"_"+PermissionConstants.SHIFT_SETUP;
    }

    /**
     * Shift Create API.
     * <p>
     * * This api is used to create shift along with shift config day.
     * </p>
     *
     * @param data
     * @screen Shift
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ShiftPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Shift shift = shiftService.create(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            shift.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Shift Update API.
     * <p>
     * * This api is used to update Shift along with days.
     * </p>
     *
     * @param data
     * @screen Shift
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody ShiftPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Shift shift = shiftService.update(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            shift.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Shift Get API.
     * <p>
     * * This api is used to get shift by id.
     * </p>
     *
     * @param id
     * @screen Shift
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        ShiftPojo data = shiftService.findById(id);
        if (data != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                            data)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName)),
                            null)
            );
        }
    }

    /**
     * Shift Get ALL API.
     * <p>
     * * This api is used to get all shift by fiscal year.
     * </p>
     *
     * @param fiscalYear
     * @screen Shift
     */
    @GetMapping(value = "/fiscal-year/{fiscalYear}")
    public ResponseEntity<?> getAllFiscalYear(@PathVariable Long fiscalYear) {
        List<ShiftPojo> data = shiftService.getAllCustomEntity(fiscalYear);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        data
                )
        );
    }

    @GetMapping(value = "/employee-code/{employeeCode}")
    public ResponseEntity<?> getAllFiscalYear(@PathVariable String employeeCode) {

        ShiftDetailPojo data = shiftService.getShiftByEmployeeCode(employeeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        data
                )
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllByOfficeCode() {
        List<ShiftPojo> data = shiftService.getAllByOfficeCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        data
                )
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        shiftService.deleteById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName)),
                        null
                )
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @GetMapping(value = "/toggle/{id}")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        boolean status = shiftService.changeStatus(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(status ? "crud.active" : "crud.inactive", moduleName),
                        null)
        );
    }

    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ShiftPojo> page = shiftService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @GetMapping("/year-month")
    public ResponseEntity<?> getShiftByYearAndMonth(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "month") String month,@RequestParam(name = "year") String year){
        List<ShiftPojo> shiftPojos = shiftService.getShiftByMonthAndYear(pisCode,month,year);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        shiftPojos)
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getLeaveByDateRange(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){
        List<ShiftPojo> shiftPojos = shiftService.getShiftByDateRange(pisCode,from,to);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        shiftPojos)
        );
    }

}
