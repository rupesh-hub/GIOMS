package com.gerp.attendance.controller.Shift;

import com.gerp.attendance.Pojo.ShiftMappingConfigPojo;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.Pojo.shift.mapped.ShiftMappedResponsePojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.service.ShiftMappingService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/shift-mapping")
public class ShiftMappingController extends BaseController {

    private final ShiftMappingService shiftMappingService;
    private final CustomMessageSource customMessageSource;

    public ShiftMappingController(ShiftMappingService shiftMappingService, CustomMessageSource customMessageSource) {
        this.shiftMappingService = shiftMappingService;
        this.customMessageSource = customMessageSource;
        this.moduleName= PermissionConstants.SHIFT_MAPPING_MODULE_NAME;
        this.permissionName= PermissionConstants.SHIFT+"_"+PermissionConstants.SHIFT_SETUP;
    }

    /**
     * Shift Mapping Create API.
     * <p>
     * * This api is used to get Map shift to employee or shift employee group.
     * </p>
     *
     * @param data
     * @screen Shift
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> saveEmployee(@Valid @RequestBody ShiftMappingConfigPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            shiftMappingService.save(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.save", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Shift Employee Group Unused Get All API.
     * <p>
     * * This api is used to get unused Shift Employee Group for office.
     * </p>
     *
     * @param shiftId
     * @screen Shift
     */
    @GetMapping(value = "/get-unused-group/{shiftId}")
    public ResponseEntity<?> getUnusedShiftGroup(@PathVariable Long shiftId) {
        List<ShiftEmployeeGroupPojo> list = shiftMappingService.getUnusedShiftGroup(shiftId);
        if (list != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                            list)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName)),
                            null)
            );
        }
    }

    /**
     * Mapped Group ID Get All API.
     * <p>
     * * This api is used to get Mapped Group Id for office.
     * </p>
     *
     * @param shiftId
     * @screen Shift
     */
    @GetMapping(value = "/get-mapped-group/shift-id/{shiftId}")
    public ResponseEntity<?> getMappedGroupIds(@PathVariable Long shiftId) {
        List<Long> list = shiftMappingService.getMappedGroupIds(shiftId);
        if (list != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("group.shift.mapping")),
                            list)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get("group.shift.mapping")),
                            null)
            );
        }
    }

    /**
     * Mapped Group ID Get All API.
     * <p>
     * * This api is used to get Mapped Group Id for office.
     * </p>
     *
     * @param shiftId
     * @screen Shift
     */
    @GetMapping(value = "/get-mapped-employee/shift-id/{shiftId}")
    public ResponseEntity<?> getMappedPisCodeIds(@PathVariable Long shiftId) {
        List<String> list = shiftMappingService.getMappedPisCodeIds(shiftId);
        if (list != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("employee.shift.mapping")),
                            list)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get("employee.shift.mapping")),
                            null)
            );
        }
    }

    /**
     * Mapped Group and Employee API.
     * <p>
     * * This api is used to get Mapped Group and Employee and shiftId for office.
     * </p>
     *
     * @param shiftId
     * @screen Shift
     */
    @GetMapping(value = "/get-mapped-detail/shift-id/{shiftId}")
    public ResponseEntity<?> getMappedDetail(@PathVariable Long shiftId) {
        ShiftMappedResponsePojo mappedDetailPojo = shiftMappingService.getMappedDetail(shiftId);
        if (mappedDetailPojo != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get_all", customMessageSource.get("employee.shift.mapping")),
                            mappedDetailPojo)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get("employee.shift.mapping")),
                            null)
            );
        }
    }

    /**
     * Remove Mapped Group API.
     * <p>
     * * This api is used to remove Mapped Group by shiftId and groupId for office.
     * </p>
     *
     * @param shiftId
     * @param groupId
     * @screen Shift
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value = "/shift-id/{shiftId}/group-id/{groupId}")
    public ResponseEntity<?> removeMappedGroup(@PathVariable Long shiftId, @PathVariable Long groupId) {
        shiftMappingService.removeMappedGroup(shiftId, groupId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName)),
                        null
                )
        );
    }

    /**
     * Remove Mapped Employee API.
     * <p>
     * * This api is used to remove Mapped Employee by shiftId and employeeId for office.
     * </p>
     *
     * @param shiftId
     * @param pisCode
     * @screen Shift
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value = "/shift-id/{shiftId}/pis-code/{pisCode}")
    public ResponseEntity<?> removeMappedEmployee(@PathVariable Long shiftId, @PathVariable String pisCode) {
        shiftMappingService.removeMappedEmployee(shiftId, pisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName)),
                        null
                )
        );
    }

//    @PutMapping(value="/transfer-shift")
//    public ResponseEntity<?> transferShift(@Valid @RequestBody ShiftMappingConfigPojo employeeShiftConfigPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            EmployeeShiftConfig employeeShiftConfig= employeeShiftConfigService.transferShift(employeeShiftConfigPojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.transfer.shift", customMessageSource.get(moduleName.toLowerCase())),
//                            employeeShiftConfig.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }

}
