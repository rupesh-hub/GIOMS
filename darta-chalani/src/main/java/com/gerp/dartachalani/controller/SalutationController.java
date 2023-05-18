package com.gerp.dartachalani.controller;

import com.gerp.dartachalani.dto.SalutationPojo;
import com.gerp.dartachalani.service.DashboardService;
import com.gerp.dartachalani.service.SalutationService;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.utils.CrudMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salutation")
public class SalutationController extends BaseController {
    private final SalutationService salutationService;

    public SalutationController(DashboardService dashboardService, SalutationService salutationService) {
        this.salutationService = salutationService;
        this.moduleName = "Salutation";
    }

    @PostMapping
    @Operation(summary = "create Salutation ", tags = {"Salutation"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SalutationPojo.class))})
    public ResponseEntity<GlobalApiResponse> addStandardTemplate(@RequestBody SalutationPojo standardTemplatePojo, BindingResult bindingResult)throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create, customMessageSource.get(moduleName)),
                            salutationService.addSalutation(standardTemplatePojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping
    @Operation(summary = "update Salutation ", tags = {"Salutation"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SalutationPojo.class))})
    public ResponseEntity<GlobalApiResponse> updateStandardTemplate(@RequestBody SalutationPojo standardTemplatePojo, BindingResult bindingResult)throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName)),
                            salutationService.updateSalutation(standardTemplatePojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "get salutation", tags = {"Salutation"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SalutationPojo.class))})
    public ResponseEntity<GlobalApiResponse> getStandardTemplate(@RequestParam(required = false) String pisCode){
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                            salutationService.getSalutation(pisCode)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete salutation by id", tags = {"Salutation"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return nothing", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SalutationPojo.class))})
    public ResponseEntity<GlobalApiResponse> deleteStandardTemplateById(@PathVariable Long id){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete, customMessageSource.get(moduleName)),
                        salutationService.deleteSalutation(id)));
    }

}
