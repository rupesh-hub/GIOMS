package com.gerp.attendance.controller.Shift.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.shift.group.ShiftEmployeeGroup;
import com.gerp.attendance.repo.ShiftEmployeeGroupConfigRepo;
import com.gerp.attendance.service.ShiftEmployeeGroupService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/shift-employee-group")
public class ShiftEmployeeGroupController extends BaseController {

    private final ShiftEmployeeGroupService shiftEmployeeGroupService;
    private final ShiftEmployeeGroupConfigRepo shiftEmployeeGroupConfigRepo;
    private final CustomMessageSource customMessageSource;

    public ShiftEmployeeGroupController(ShiftEmployeeGroupService shiftEmployeeGroupService,ShiftEmployeeGroupConfigRepo shiftEmployeeGroupConfigRepo, CustomMessageSource customMessageSource) {
        this.shiftEmployeeGroupService = shiftEmployeeGroupService;
        this.customMessageSource = customMessageSource;
        this.shiftEmployeeGroupConfigRepo = shiftEmployeeGroupConfigRepo;
        this.moduleName = PermissionConstants.SHIFT_EMPLOYEE_GROUP_MODULE_NAME;
        this.permissionName = PermissionConstants.SHIFT + "_" + PermissionConstants.SHIFT_EMPLOYEE_GROUP;
    }
    /**
     * Shift Employee Group Create API.
     * <p>
     * * This api is used to create Shift Employee Group alone with employee mapping.
     * </p>
     *
     * @param data
     * @screen Shift
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ShiftEmployeeGroupPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            ShiftEmployeeGroup shiftEmployeeGroup = shiftEmployeeGroupService.create(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            shiftEmployeeGroup.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Shift Employee Group Update API.
     * <p>
     * * This api is used to update Shift Employee Group alone with employee mapping.
     * </p>
     *
     * @param data
     * @screen Shift
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody ShiftEmployeeGroupPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            ShiftEmployeeGroup shiftEmployeeGroup = shiftEmployeeGroupService.update(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            shiftEmployeeGroup.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Shift Employee Group Get API.
     * <p>
     * * This api is used to get Shift Employee Group.
     * </p>
     *
     * @param id
     * @screen Shift
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        ShiftEmployeeGroupPojo data = shiftEmployeeGroupService.findByIdCustom(id);
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
     * Shift Employee Group Get ALL API.
     * <p>
     * * This api is used to get all Shift Employee Group by fiscal year.
     * </p>
     *
     * @param fiscalYear
     * @screen Shift
     */
    @GetMapping(value = "/fiscal-year/{fiscalYear}")
    public ResponseEntity<?> getAllFiscalYear(@PathVariable Long fiscalYear) {
        List<ShiftEmployeeGroupPojo> data = shiftEmployeeGroupService.getAllCustomEntity(fiscalYear);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        data
                )
        );
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<ShiftEmployeeGroupPojo> data = shiftEmployeeGroupService.getAllCustom();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        data
                )
        );
    }

    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ShiftEmployeeGroupPojo> page = shiftEmployeeGroupService.getAllByFiscalYear(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        shiftEmployeeGroupConfigRepo.deleteByShiftGroup(id);
        shiftEmployeeGroupService.deleteById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName)),
                        null
                )
        );
    }

    /**
     * Employee PisCode Used By Office.
     * <p>
     * * This api is used get pis-code used by office.
     * </p>
     *
     * @param
     */
    @GetMapping(value = "/used-pis-code-office")
    public ResponseEntity<?> getUsedPisCodeForOffice() {
        List<String> pisCodes = shiftEmployeeGroupService.getUsedPisCodeForOffice();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        pisCodes
                )
        );
    }
}
