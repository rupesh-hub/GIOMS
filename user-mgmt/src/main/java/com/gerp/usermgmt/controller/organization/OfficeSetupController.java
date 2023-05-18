package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.enums.OfficeSetupStepEnum;
import com.gerp.usermgmt.pojo.organization.office.OfficeSetupPojo;
import com.gerp.usermgmt.services.organization.office.OfficeSetupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/office-setup")
public class OfficeSetupController extends BaseController {

    private final OfficeSetupService officeSetupService;

    private final CustomMessageSource customMessageSource;

    public OfficeSetupController(OfficeSetupService  officeSetupService, CustomMessageSource customMessageSource) {
        this.officeSetupService = officeSetupService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.OFFICE_SETUP;
    }

    @GetMapping("/step-status")
    public ResponseEntity<?> getStatus(@RequestParam("step") String step){
        OfficeSetupPojo officeSetupSTatusPojo = officeSetupService.findStepStatus(step);
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", OfficeSetupStepEnum.valueOf(step)),officeSetupSTatusPojo));
    }

    @PutMapping ("/update-steps-status")
    public ResponseEntity<?> updateStatus(@RequestBody OfficeSetupPojo officeSetupPojo){
        Long officeSetupId = officeSetupService.updateOfficeSetup(officeSetupPojo);

        return  ResponseEntity.ok(successResponse(customMessageSource.get("crud.update", OfficeSetupStepEnum.valueOf(officeSetupPojo.getOfficeSetupStep().toString())), officeSetupId));
    }

    @GetMapping("/all-step-status")
    public ResponseEntity<?> getAllStepStatus(){
        List<OfficeSetupPojo> officeSetupPojos = officeSetupService.findAllOfficeStepStatus();
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", moduleName), officeSetupPojos));
    }
}
