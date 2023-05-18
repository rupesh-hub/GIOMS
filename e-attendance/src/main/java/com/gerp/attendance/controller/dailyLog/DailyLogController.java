package com.gerp.attendance.controller.dailyLog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.DailyLogPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.dailyLog.DailyLog;
import com.gerp.attendance.service.DailyLogService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ApprovalParamStatus;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.ApprovalPojo;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/daily-log")
public class DailyLogController extends BaseController {

    private final DailyLogService dailyLogService;
    private final CustomMessageSource customMessageSource;

    public DailyLogController(DailyLogService dailyLogService,
                              CustomMessageSource customMessageSource) {
        this.dailyLogService = dailyLogService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.DAILY_LOG_MODULE_NAME;
        this.moduleName2 =PermissionConstants.DAILY_LOG_REQUEST_MODULE_NAME;
        this.permissionName = PermissionConstants.DAILY_LOG + "_" + PermissionConstants.DAILY_LOG_SETUP;
        this.permissionApproval = PermissionConstants.APPROVAL+"_"+PermissionConstants.DAILY_LOG_APPROVAL;
    }

    /**
     * This method adds daily log information
     * @param dailyLogPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody DailyLogPojo dailyLogPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            DailyLog dailyLog = dailyLogService.save(dailyLogPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            dailyLog.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * This method update daily log information
     * @param dailyLogPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody DailyLogPojo dailyLogPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            dailyLogService.update(dailyLogPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            dailyLogPojo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Gets all Daily Logs
     * @return
     */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        List<DailyLogPojo> dailyLogPojos = dailyLogService.getAllDailyLogs();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dailyLogPojos)
        );
    }

    /**
     * Gets Daily Log information of given employeeCode
     * @return
     */
    @GetMapping("/get-by-emp-pis-code")
    public ResponseEntity<?> getDailyLogsByPisCode() {
        List<DailyLogPojo> dailyLogPojos = dailyLogService.getDailyLogByPisCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dailyLogPojos)
        );
    }

    /**
     * Gets Daily Log information by approver pisCode
     * @return
     */
    @GetMapping("/get-by-approver-pis-code")
    public ResponseEntity<?> getDailyLogsByApproverPisCode() {
        List<DailyLogPojo> dailyLogPojos = dailyLogService.getDailyLogByApproverPisCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dailyLogPojos)
        );
    }

    /**
     * Gets Daily Log information by office Code
     * @return
     */
    @GetMapping("/get-by-office-code")
    public ResponseEntity<?> getDailyLogsByOfficeCode() {
        List<DailyLogPojo> dailyLogPojos = dailyLogService.getDailyLogByOfficeCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dailyLogPojos)
        );
    }

    /**
     * Gets Daily Information by Id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        DailyLogPojo dailyLogPojo = dailyLogService.getLogById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dailyLogPojo)
        );
    }

    /**
     * Daily Log APPROVAL API.
     * <p>
     *      * This api is used to update approval status.
     *      ? {@link ApprovalPojo}
     *      ? used for all case
     * </p>
     * @param data
     */
//    @PreAuthorize("@customPermissionEvaluator.hasPermissionStatus(#this.this.permissionName, #this.this.permissionApproval, #type.name())")
    @PutMapping(value = "/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam(value = "type") ApprovalParamStatus type, @Valid @ModelAttribute ApprovalPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {

            if(type==null){
                type = ApprovalParamStatus.update;
            }
            switch (type){
                case update:
                    if(!data.getStatus().validateUpdate())
                        throw new RuntimeException(customMessageSource.get("user.can.only.cancel"));
                    break;
                case approve:
                    if(!data.getStatus().validateApprove())
                        throw new RuntimeException(customMessageSource.get("approver.cannot.cancel"));
                    break;
                case review:
                    if(!data.getStatus().validateReview())
                        throw new RuntimeException(customMessageSource.get("reviewer.can.only.forward"));
                    break;

                case revert:
                    if(!data.getStatus().validateRevert())
                        throw new RuntimeException(customMessageSource.get("reviewer.can.only.revert"));
                    break;
            }

            data.setModule(this.module);
            data.setModuleName(this.moduleName);
            dailyLogService.updateStatus(data);

            switch (data.getStatus()){
                case A:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.approved", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                case R:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.rejected", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                case F:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                case C:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.cancel", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );

                case RV:
                    return ResponseEntity.ok(
                            successResponse(customMessageSource.get("crud.revert", customMessageSource.get(moduleName.toLowerCase())),
                                    null)
                    );
                default:
                    return ResponseEntity.ok(
                            errorResponse(customMessageSource.get("invalid.action"),
                                    null)
                    );
            }
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDelete(@PathVariable("id") Long id) {
            dailyLogService.softDelete(id);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                            id)
            );
    }

    /**
     * Paginated Data Employee
     */
    @PostMapping(value = "/employee/paginated")
    public ResponseEntity<?> getEmployeePaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setIsApprover(false);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dailyLogService.filterData(paginatedRequest))
        );
    }

    /**
     * Paginated Data Approver
     */
    @PostMapping(value = "/approver/paginated")
    public ResponseEntity<?> getApproverPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setIsApprover(true);
        Page<DailyLogPojo> page = dailyLogService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }


}
