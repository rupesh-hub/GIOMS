package com.gerp.usermgmt.controller.organization.controllerdoc;

import com.gerp.usermgmt.pojo.organization.employee.EmployeeJoiningDatePojo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Employee Joining Date", description = "This API is used to persist employee joining date")
public interface EmployeeJoiningDateDoc {

    @Operation(summary = "Save EmployeeJoiningDate",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "",
            tags = {"Employee Joining Date"}
    )
    ResponseEntity<?> save(@RequestBody EmployeeJoiningDatePojo employeeJoiningDatePojo, BindingResult bindingResult) throws BindException;

   @Operation(summary = "toggle EmployeeJoiningDate",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "",
            tags = {" toggle Employee Joining Date"}
    )
    ResponseEntity<?> toggleEmployee(@PathVariable Long id);

    @Operation(summary = "EmployeeJoiningDate list",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "Api to get list of employee",
            tags = {"employee joining date list"}
    )
    ResponseEntity<?> getJoiningDateList(@RequestParam("pisCode") String pisCode);
}
