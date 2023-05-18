package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.controller.organization.controllerdoc.EmployeeJoiningDateDoc;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeJoiningDatePojo;
import com.gerp.usermgmt.services.organization.employee.EmployeeJoiningDateService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee-joining-date")
public class EmployeeJoiningDateController extends BaseController implements EmployeeJoiningDateDoc {
    private final EmployeeJoiningDateService employeeJoiningDateService;

    public EmployeeJoiningDateController(EmployeeJoiningDateService employeeJoiningDateService) {
        this.employeeJoiningDateService = employeeJoiningDateService;
        this.moduleName = PermissionConstants.EMPLOYEE_JOINING_DATE;
    }

    @Override
    @PostMapping
    public ResponseEntity<?> save(@RequestBody EmployeeJoiningDatePojo employeeJoiningDatePojo , BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {

            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName)),
                            employeeJoiningDateService.save(employeeJoiningDatePojo))
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> toggleEmployee(@PathVariable Long id) {
//        try {
            employeeJoiningDateService.toggleJoiningDate(id);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName)),
                            null));
//        } catch (Exception ex){
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("empty.request", customMessageSource.get(moduleName)),
//                            null));
//        }
    }

    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<?> getJoiningDateList(@RequestParam("pisCode") String pisCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName)),
                        employeeJoiningDateService.getJoiningDateList(pisCode)));
    }
}
