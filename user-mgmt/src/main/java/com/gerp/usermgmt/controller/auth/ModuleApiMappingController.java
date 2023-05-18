package com.gerp.usermgmt.controller.auth;

import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.converter.ModuleApiMappingConverter;
import com.gerp.usermgmt.model.ModuleApiMapping;
import com.gerp.usermgmt.pojo.auth.ModuleApiMappingPojo;
import com.gerp.usermgmt.pojo.auth.ModuleApiMappingRequestPojo;
import com.gerp.usermgmt.repo.auth.ModuleApiMappingRepo;
import com.gerp.usermgmt.services.usermgmt.ModuleApiMappingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/auth-map")
public class ModuleApiMappingController extends GenericCrudController<ModuleApiMapping, Long> {

    private final ModuleApiMappingService moduleApiMappingService;
    @Autowired
    private ModuleApiMappingRepo moduleApiMappingRepo;

    @Autowired
    private ModuleApiMappingConverter moduleApiMappingConverter;

    @Autowired
    private ModelMapper modelMapper;

//    private ObjectMapperUtils objectMapperUtils;

    public ModuleApiMappingController(ModuleApiMappingService moduleApiMappingService) {
        this.moduleApiMappingService = moduleApiMappingService;
        this.moduleName = PermissionConstants.PRIVILEGE_MODULE_NAME;
    }


    @PostMapping("/save")
    public ResponseEntity<?> create(@Valid @RequestBody ModuleApiMappingRequestPojo moduleApiMappingRequestPojo, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                moduleApiMappingService.create(moduleApiMappingRequestPojo.getModuleApiMapping());
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                                null)
                );
            }catch (Exception e){
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("error.cant.update"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ModuleApiMappingRequestPojo moduleApiMappingRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            try {
                List<ModuleApiMapping> moduleApiMapping = moduleApiMappingConverter.toEntity(moduleApiMappingRequestPojo.getModuleApiMapping());
                moduleApiMappingService.updateMany(moduleApiMapping);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                                null)
                );
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("error.cant.update"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }

    @Override
    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ModuleApiMapping> page = moduleApiMappingService.getPaginated(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @GetMapping("/get-by-module-id/{moduleId}")
    public ResponseEntity<?> listByModuleId(@PathVariable("moduleId") Long moduleId) {
        List<ModuleApiMappingPojo> list = moduleApiMappingService.findByModuleId(moduleId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        list)
        );
    }
}
