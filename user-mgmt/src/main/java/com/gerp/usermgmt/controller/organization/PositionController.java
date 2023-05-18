package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.converter.organiztion.orgtransfer.PositionConverter;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.PositionPojo;
import com.gerp.usermgmt.services.organization.designation.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/position")
@Slf4j
public class PositionController extends GenericCrudController<FunctionalDesignation, String> {
    private final PositionService positionService;
    private final PositionConverter positionConverter;

    public PositionController(PositionService positionService, PositionConverter positionConverter) {
        this.positionService = positionService;
        this.positionConverter = positionConverter;
        this.moduleName = PermissionConstants.POSITION;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllPositions() {
        List<IdNamePojo> s = positionService.positions();
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping("/top-positions")
    public ResponseEntity<?> getAllTopPositions() {
        List<IdNamePojo> s = positionService.topParentPosition();
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping("/office-positions")
    public ResponseEntity<GlobalApiResponse> getOfficePositions() {
        List<IdNamePojo> position = positionService.getOfficePositions();
        return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),position ));
    }

    @PostMapping(value = "save")
    public ResponseEntity<?> create(@Valid @RequestBody PositionPojo position, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                String t = positionService.save(position);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                                t)
                );
            }catch (Exception e){
                log.error(e.getMessage());
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }
    @PutMapping(value = "update")
    public ResponseEntity<?> update(@Valid @RequestBody PositionPojo position, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                String t = positionService.update(position);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                                t)
                );
            }catch (Exception e){
                log.error(e.getMessage());
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }

    @PostMapping("/get-all-filtered")
    public ResponseEntity<?> getFilteredDesignation(@RequestBody SearchPojo searchPojo) {
        List<PositionPojo> s = positionService.positionSearch(searchPojo);
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        PositionPojo p = positionConverter.toDto(positionService.findById(id));
        if (p != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            p)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }

    }

}
