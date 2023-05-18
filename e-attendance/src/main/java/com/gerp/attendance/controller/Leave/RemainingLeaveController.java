package com.gerp.attendance.controller.Leave;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceMonthlyReportPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.leave.RemainingLeave;
import com.gerp.attendance.service.RemainingLeaveService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/remaining-leave")
public class RemainingLeaveController extends BaseController {

    private final RemainingLeaveService remainingLeaveService;
    private final CustomMessageSource customMessageSource;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    public RemainingLeaveController(RemainingLeaveService remainingLeaveService, CustomMessageSource customMessageSource) {
        this.remainingLeaveService = remainingLeaveService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.REMAINING_LEAVE_MODULE_NAME;
        this.permissionName = PermissionConstants.LEAVE + "_" + PermissionConstants.REMAINING_LEAVE_SETUP;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RemainingLeavePojo remainingLeavePojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            RemainingLeave remainingLeave = remainingLeaveService.save(remainingLeavePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            remainingLeave.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }


    @GetMapping(value = "/schedular-check")
    public ResponseEntity<?> updateRemaining() {

        remainingLeaveService.updateRemainingLeaveDaily();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                        "successfull")
        );

    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody RemainingLeavePojo remainingLeavePojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
//            RemainingLeave remainingLeave = remainingLeaveService.updateRemainingLeave(remainingLeavePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            remainingLeaveService.updateRemainingLeaveNew(remainingLeavePojo))
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {

        RemainingLeave remainingLeave = remainingLeaveService.findById(id);
        if (remainingLeave != null) {
            remainingLeaveService.deleteRemainingLeave(id);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    //TODO changes during year change
    @GetMapping(value = "{id}")
    public ResponseEntity<?> getRemainingLeaveById(@PathVariable Long id, @RequestParam(defaultValue = "year") String year) {
        RemainingLeaveResponsePoio remainingLeaveResponsePoio = remainingLeaveService.getRemainingLeaveById(id, year);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        remainingLeaveResponsePoio)
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllRemainingLeave() {
        ArrayList<RemainingLeaveMinimalPojo> remainingLeaveMinimalPojos = remainingLeaveService.getAllRemainingLeave();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        remainingLeaveMinimalPojos)
        );
    }

    // TODO changes during year change
    @GetMapping(value = "/get-by-office-code")
    public ResponseEntity<?> getRemainingLeaveByOfficeCode(@RequestParam(name = "name") String name, @RequestParam(defaultValue = "5") Integer fiscalYear, @RequestParam(defaultValue = "2078") String year) {
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        remainingLeaveService.getByOfficeCode(name, year))
//        );

        //TODO: optimized one for testing purpose only
                return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        remainingLeaveService.remainingLeaveByOfficeCode(name, year))
        );
    }


    @GetMapping(value = "/employee/leaveTaken")
    public ResponseEntity<?> getEmployeeLeaveTaken(@RequestParam(name = "name") String name) {
        List<EmployeeLeaveTakenPojo> employeeLeaveTakenPojos = remainingLeaveService.employeeLeaveTaken(name);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        employeeLeaveTakenPojos)
        );
    }


    @PostMapping(value = "/yearly-remaining-leave")
    public ResponseEntity<?> getYearlyAttendance(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeeAttendanceMonthlyReportPojo> page = remainingLeaveService.filterRemainingLeave(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }


    //TODO changes during year change

    /**
     * changes - pis code will be assigned default when user do not provided it
     *
     * @param pisCode
     * @param fiscalYear
     * @param year
     * @return
     */
    @GetMapping(value = "/get-all-remainingleave/{pisCode}")
    public ResponseEntity<?> getAllRemainingByPisCode(@PathVariable String pisCode,
                                                      @RequestParam(defaultValue = "6") Integer fiscalYear,
                                                      @RequestParam(defaultValue = "2079") String year) {
        if (pisCode.equals("null"))
            pisCode = tokenProcessorService.getPisCode();

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        remainingLeaveService.getLeaveOfPisCode(pisCode, year))
        );

    }

    @GetMapping(value = "/get-remaining-leave")
    public ResponseEntity<?> getRemainingLeaveByPis(@RequestParam(defaultValue = "null") String year) {

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        remainingLeaveService.getRemainingLeave(tokenProcessorService.getPisCode(), year))
        );

    }

    @GetMapping(value = "/get-by-piscode/{leavePolicyId}")
    public ResponseEntity<?> getRemainingLeaveByPisCode(@PathVariable Long leavePolicyId,
                                                        @RequestParam(defaultValue = "null") String pisCode,
                                                        @RequestParam(defaultValue = "Remaining") String type,
                                                        @RequestParam(defaultValue = "2080") String year,
                                                        @RequestParam String fromDate,
                                                        @RequestParam String toDate) {
        if (pisCode.equals("null"))
            pisCode = tokenProcessorService.getPisCode();


        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        remainingLeaveService.getByPisCode(leavePolicyId, pisCode, type, year, fromDate, toDate)));
    }

    @GetMapping(value = "/get-by-specific-piscode")
    public ResponseEntity<?> getRemainingBySpecificPisCode(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "leavePolicyId") Long leavePolicyId, @RequestParam(defaultValue = "Remaining") String type, @RequestParam(defaultValue = "2079") String year,
                                                           @RequestParam(name = "fromDate") String fromDate,
                                                           @RequestParam(name = "toDate") String toDate) {
        RemainingLeaveForLeaveRequestPojo remainingLeave = remainingLeaveService.getByPisCode(leavePolicyId, pisCode, type, year, fromDate, toDate);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        remainingLeave)
        );
    }


    // for inserting data
    @PostMapping(value = "/get-transformed-excel")
    public ResponseEntity<?> getTransformedExcel(@ModelAttribute RemainingLeaveDocRequestPojo remainingRequestPojo) throws
            IOException {
        return ResponseEntity.ok(remainingLeaveService.readTransformedExcel(remainingRequestPojo));
    }


    @GetMapping(value = "/employee-remaining-update")
    public ResponseEntity<?> getEmployeeRemainingUpdate() {
        remainingLeaveService.updateRemainingLeaveDaily();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        "remaining leave updated")
        );
    }

//    @GetMapping(value = "/karar-employee-update")
//    public ResponseEntity<?> getKararEmployeeUpdate(@RequestParam("pisCode")String pisCode,@RequestParam("fromDate")LocalDate fromDate,@RequestParam("toDate")LocalDate toDate) {
//        remainingLeaveService.updateKararEmployee(pisCode,fromDate,toDate);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
//                        "remaining leave updated")
//        );
//    }


    @GetMapping(value = "/karar-employee")
    public ResponseEntity<?> updateKararEmployee(@RequestParam(name = "pisCode") String pisCode, @RequestParam(name = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate, @RequestParam(name = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        String employeeLeaveTakenPojos = remainingLeaveService.updateKarar(pisCode, fromDate, toDate);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                        employeeLeaveTakenPojos)
        );
    }

    @GetMapping(value = "/update-yearly")

    public ResponseEntity<?> getYearly() {
        remainingLeaveService.remainingLeave();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), true
                )
        );
    }

    @GetMapping(value = "/getUnInformLeave")
    public ResponseEntity<?> getUnInformLeave() {
        int totalUnInformLeave = remainingLeaveService.unInformLeaveCount();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        totalUnInformLeave)
        );
    }


    @GetMapping(value = "/getHomeLeaveDetail/{pisCode}")
    public ResponseEntity<?> getHomeLeaveDetail(@PathVariable String pisCode) {
        HomeLeavePojo homeLeavePojo = remainingLeaveService.getHomeLeave(pisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        homeLeavePojo)
        );
    }
    @GetMapping(value = "/schedular/{pisCode}")
    public ResponseEntity<?> updateRemainingByPisCode(@PathVariable String pisCode) {
        remainingLeaveService.updateRemainingLeaveDailyByPisCode(pisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                        "successfull")
        );

    }
}
