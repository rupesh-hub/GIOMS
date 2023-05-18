package com.gerp.attendance.controller.Attendance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.ManualAttendanceMapperPojo;
import com.gerp.attendance.Pojo.ManualAttendancePojo;
import com.gerp.attendance.Pojo.ManualAttendanceUpdatePojo;
import com.gerp.attendance.Pojo.ManualPracticePojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.attendances.ManualAttendance;
import com.gerp.attendance.service.ExcelService;
import com.gerp.attendance.service.FileStorageService;
import com.gerp.attendance.service.ManualAttendanceService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ApprovalParamStatus;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.ApprovalPojo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author info
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/manual-attendance")
public class ManualAttendanceController extends BaseController {
    private final ManualAttendanceService manualAttendanceService;
    private final CustomMessageSource customMessageSource;
    private final FileStorageService fileStorageService;
    private final ExcelService excelService;

    public ManualAttendanceController(ManualAttendanceService manualAttendanceService, FileStorageService fileStorageService, ExcelService excelService, CustomMessageSource customMessageSource) {
        this.manualAttendanceService = manualAttendanceService;
        this.customMessageSource = customMessageSource;
        this.fileStorageService = fileStorageService;
        this.excelService = excelService;
        this.moduleName = PermissionConstants.MANUAL_ATTENDANCE_MODULE_NAME;
        this.permissionName = PermissionConstants.ATTENDANCE + "_" + PermissionConstants.MANUAL_ATTENDANCE_SETUP;
        this.permissionApproval = PermissionConstants.APPROVAL + "_" + PermissionConstants.MANUAL_ATTENDANCE_APPROVAL;
    }

    /**
     * This method adds attendance of employees manually
     *
     * @param manualAttendancePojo
     * @param bindingResult
     * @return
     * @throws BindException
     * @throws IOException
     */
    @PostMapping("/save")
    public ResponseEntity<?> create(@Valid @ModelAttribute ManualAttendancePojo manualAttendancePojo, BindingResult bindingResult) throws BindException, IOException {

        if (!bindingResult.hasErrors()) {
            ManualAttendance manualAttendance = manualAttendanceService.saveAttendance(manualAttendancePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            manualAttendance.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/saveNew")
    public ResponseEntity<?> creates(@Valid @ModelAttribute ManualAttendancePojo manualAttendancePojo, BindingResult bindingResult) throws BindException, IOException {

        if (!bindingResult.hasErrors()) {
            ManualAttendance manualAttendance = manualAttendanceService.saveAttendanceBulk(manualAttendancePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            manualAttendance.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws Exception {

        Resource resource = fileStorageService.loadFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * This method download the template for manual attendance
     *
     * @return
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> getFile() {
        String filename = "attendance.xlsx";
        InputStreamResource file = new InputStreamResource(excelService.load());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/xlsx"))
                .body(file);
    }

    /**
     * This method updates the manual attendance
     *
     * @param manualAttendanceUpdatePojo
     * @param bindingResult
     * @return
     * @throws BindException
     * @throws IOException
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @ModelAttribute ManualAttendanceUpdatePojo manualAttendanceUpdatePojo, BindingResult bindingResult) throws BindException, IOException {

        if (!bindingResult.hasErrors()) {
            ManualAttendance manualAttendance = manualAttendanceService.updateAttendance(manualAttendanceUpdatePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            manualAttendance.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Gets all manual attendance respective of office
     *
     * @return
     */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllManualAttendance() {
        ArrayList<ManualAttendancePojo> manualAttendancePojos = manualAttendanceService.getAllManualAttendance();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        manualAttendancePojos)
        );
    }

    /**
     * Gets Attendance of Employee by Id
     *
     * @param id
     * @return
     */
    @GetMapping("get-by-id/{id}")
    public ResponseEntity<?> getManualAttendanceById(@PathVariable("id") Long id) {
        ManualAttendancePojo manualAttendancePojo = manualAttendanceService.getManualAttendanceById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        manualAttendancePojo)
        );
    }

    /**
     * Soft Delete Manual Attendance
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManualAttendance(@PathVariable("id") Long id) {
        manualAttendanceService.deleteManualAttendance(id);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        null
                )
        );
    }

    /**
     * Gets Employees Attendance By Approver PisCode
     *
     * @return
     */
    @GetMapping(value = "/get-by-approver-pis-code")
    public ResponseEntity<?> getManualAttendanceByApproverPisCode() {
        ArrayList<ManualAttendancePojo> manualAttendanceMapperPojos = manualAttendanceService.getManualAttendanceByApproverPisCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        manualAttendanceMapperPojos)
        );

    }

    /**
     * Gets Employee Attendance By OfficeId
     *
     * @param officeId
     * @return
     */
    @GetMapping("get-by-office-id/{officeId}")
    public ResponseEntity<?> getAttendanceByOfficeId(@PathVariable("officeId") Integer officeId) {
        ArrayList<ManualAttendanceMapperPojo> manualAttendanceMapperPojo = manualAttendanceService.getByOfficeId(officeId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        manualAttendanceMapperPojo)
        );
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> saveFile(@Valid @ModelAttribute ManualPracticePojo manualPracticePojo, BindingResult bindingResult) throws BindException, IOException {

        if (!bindingResult.hasErrors()) {
            ManualPracticePojo manualPracticePojo1 = manualAttendanceService.saveAttendanceFile(manualPracticePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            manualPracticePojo1)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Manual Attendance APPROVAL API.
     * <p>
     * * This api is used to update approval status.
     * ? {@link ApprovalPojo}
     * ? used for all case
     * </p>
     *
     * @param data
     */
    @PutMapping(value = "/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam(value = "type") ApprovalParamStatus type, @Valid @ModelAttribute ApprovalPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {

            if (type == null) {
                type = ApprovalParamStatus.update;
            }
            switch (type) {
                case update:
                    if (!data.getStatus().validateUpdate())
                        throw new RuntimeException(customMessageSource.get("user.can.only.cancel"));
                    break;
                case approve:
                    if (!data.getStatus().validateApprove())
                        throw new RuntimeException(customMessageSource.get("approver.cannot.cancel"));
                    break;
                case review:
                    if (!data.getStatus().validateReview())
                        throw new RuntimeException(customMessageSource.get("reviewer.can.only.forward"));
                    break;
            }

            data.setModule(this.module);
            data.setModuleName(this.moduleName);
            manualAttendanceService.updateStatus(data);
            switch (data.getStatus()) {
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

    /**
     * Paginated Data Employee
     */
    @PostMapping(value = "/employee/paginated")
    public ResponseEntity<?> getEmployeePaginated(@RequestBody GetRowsRequest paginatedRequest) {
//        Page<ManualAttendancePojo> page = manualAttendanceService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        manualAttendanceService.manualAttendanceFilter(paginatedRequest))
        );
    }

    /**
     * Paginated Data Approver
     */
    @PostMapping(value = "/approver/paginated")
    public ResponseEntity<?> getApproverPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        paginatedRequest.setIsApprover(true);
//        Page<ManualAttendancePojo> page = manualAttendanceService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        manualAttendanceService.manualAttendanceFilter(paginatedRequest))
        );
    }

}
