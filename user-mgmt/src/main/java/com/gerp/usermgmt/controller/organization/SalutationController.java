package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.SalutationPojo;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.services.SalutationService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class SalutationController extends BaseController {

    private final SalutationService salutationService;

    public SalutationController(SalutationService salutationService) {
        this.salutationService = salutationService;
        this.moduleName = "Salutation";
    }

    @PostMapping("/salutation-details")
    public ResponseEntity<GlobalApiResponse> getSalutationDetails(@RequestBody List<SalutationPojo> salutationPojos){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                        salutationService.getSalutation(salutationPojos)));
    }
}
