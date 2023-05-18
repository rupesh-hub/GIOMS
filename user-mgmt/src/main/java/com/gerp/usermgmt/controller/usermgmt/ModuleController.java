package com.gerp.usermgmt.controller.usermgmt;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.ModuleDto;
import com.gerp.usermgmt.services.usermgmt.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/modules")
public class ModuleController extends BaseController {
    private final ModuleService moduleService;
    private final CustomMessageSource customMessageSource;

    public ModuleController(ModuleService moduleService, CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.MODULE_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.MODULE + "_" + PermissionConstants.MODULE_SETUP;
        this.moduleService = moduleService;
    }


    /**
     * Create and update module
     *
     * @param moduleDto
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> createModule(@Valid @RequestBody ModuleDto moduleDto,
                                          BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            moduleDto = moduleService.createModule(moduleDto);
            return new ResponseEntity(successResponse(customMessageSource.get("crud.create",
                    customMessageSource.get(moduleName)), moduleDto), HttpStatus.OK);
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Find all module
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> findAll() {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get_all",
                customMessageSource.get(moduleName)), moduleService.findAllModule()), HttpStatus.OK);

    }

    @GetMapping("{moduleId}")
    public ResponseEntity<?> findById(@PathVariable("moduleId") Long moduleId) {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get",
                customMessageSource.get(moduleName)), moduleService.findModuleById(moduleId)), HttpStatus.OK);
    }

    @GetMapping("/getById/{moduleId}")
    public ResponseEntity<?> findModuleById(@PathVariable("moduleId") Long moduleId) {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get",
                customMessageSource.get(moduleName)), moduleService.findModuleByModuleId(moduleId)), HttpStatus.OK);
    }


    @GetMapping("/individual-screen/{individualScreen}")
    public ResponseEntity<?> getModulesByIndividualScreen(@PathVariable("individualScreen") Long individualScreen) {
        List<ModuleDto> moduleDtoList = moduleService.findByScreen(individualScreen);
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName)), moduleDtoList), HttpStatus.OK);
    }

    @GetMapping("/screens")
    public ResponseEntity<?> getScreenModules() {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get_all",
                "Screen module list"), moduleService.screensModuleList()), HttpStatus.OK);
    }

    @GetMapping("/get-by-module-name/{moduleName}")
    public ResponseEntity<?> getModuleByModuleName(@PathVariable("moduleName") String moduleName) {
        return new ResponseEntity(successResponse(customMessageSource.get("crud.get",
                customMessageSource.get("module")), moduleService.findModuleByModuleKey(moduleName)), HttpStatus.OK);
    }

//    @GetMapping("/get-id-by-module-name/{moduleName}")
//    public ResponseEntity<?> getModuleIdByModuleName(@PathVariable("moduleName") String moduleName) {
//        return new ResponseEntity(successResponse(customMessageSource.get("crud.get",
//                customMessageSource.get("module")), moduleService.findModuleByModuleName(moduleName).getId()), HttpStatus.OK);
//    }


}
