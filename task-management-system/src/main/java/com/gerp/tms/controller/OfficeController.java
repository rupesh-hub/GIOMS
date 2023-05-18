package com.gerp.tms.controller;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.PermissionConstants;
import com.gerp.tms.pojo.response.OfficeWiseProjectResponsePojo;
import com.gerp.tms.service.OfficeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/office")
@RestController
public class OfficeController extends BaseController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
        this.moduleName = PermissionConstants.OFFICE;
    }

    @GetMapping("/projects")
    @Operation(summary = "Get all the project according to all office", tags = {"Office"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OfficeWiseProjectResponsePojo.class))})
    public ResponseEntity<?> getOfficeProject(@RequestParam(required = false) String officeId){

        List<OfficeWiseProjectResponsePojo> projects = officeService.getOfficeWiseProject( officeId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        projects));
    }
}
