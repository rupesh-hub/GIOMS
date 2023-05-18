package com.gerp.usermgmt.controller.usermgmt;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.IndividualScreenDto;
import com.gerp.usermgmt.services.usermgmt.IndividualScreenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/individual-screens")
public class IndividualScreenController extends BaseController {
    private final IndividualScreenService individualScreenService;
    private final CustomMessageSource customMessageSource;

    public IndividualScreenController(IndividualScreenService individualScreenService,
                                      CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.INDIVIDUAL_SCREEN_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.SCREEN_SETUP + "_" + PermissionConstants.INDIVIDUAL_SCREEN_SETUP;
        this.individualScreenService = individualScreenService;
    }

//    @PreAuthorize("{hasPermission(#this.this.permissionName,'create')}")
    @PostMapping
    public ResponseEntity<?> saveIndividualScreen(@Valid @RequestBody IndividualScreenDto individualScreenDto, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            individualScreenDto = individualScreenService.createIndividualScreen(individualScreenDto);
            return new ResponseEntity(successResponse(customMessageSource.get("crud.create",
                    customMessageSource.get(moduleName)), individualScreenDto), HttpStatus.OK);
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    public ResponseEntity<?> findAllScreens() {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get_all",
                customMessageSource.get(moduleName)), individualScreenService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findIndividualScreenById(@PathVariable("id") Long individualScreenId) {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get",
                customMessageSource.get(moduleName)), individualScreenService.findById(individualScreenId)), HttpStatus.OK);
    }

    @GetMapping("/screen-group/{id}")
    public ResponseEntity<?> findIndividualScreenByScreenGroupId(@PathVariable("id") Long screenGroupId) {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get",
                customMessageSource.get(moduleName)), individualScreenService.findByScreenGroupId(screenGroupId)), HttpStatus.OK);
    }
}
