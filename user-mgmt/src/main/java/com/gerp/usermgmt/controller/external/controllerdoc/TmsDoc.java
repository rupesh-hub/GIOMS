package com.gerp.usermgmt.controller.external.controllerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Task Management System", description = "This API is used to persist Task Management System")
public interface TmsDoc {

    @Operation(summary = " Get Office List ",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "This api is used to get all the office list for TMS ",
            tags = {"office list for tms application "}
    )
    ResponseEntity<?> getOfficeListForTMSApplication();

    @Operation( summary = "Get Employee List ",
            security =  {@SecurityRequirement(name = "oauth2")},
            description = "This api is used to get all the employee of given officeCode",
            tags = "employee list by office for tms application"
    )
    ResponseEntity<?> getEmployeeListByOfficeForTMSApplication(@PathVariable("officeCode") String officeCode);

    @Operation( summary = "Get all the screen module",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "This api is used to get all the screen module",
            tags="get Screen module list"
    )
    ResponseEntity<?> getScreenModuleList();

    @Operation(summary = "Get all the screen module for logged in user",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "This api is used to get all the screen module for the user specific",
            tags = "get screen module list by logged in user"
    )
    ResponseEntity<?> getScreenModuleListByLoggedInUser();

    @Operation(summary = "Get all the GIOMS",
            security = {@SecurityRequirement(name = "oauth2")},
            description = "This api is used to get the list of GOIMS user",
            tags = "get all the user "
    )
    public ResponseEntity<?> getAllEmployeeNameList();
}
