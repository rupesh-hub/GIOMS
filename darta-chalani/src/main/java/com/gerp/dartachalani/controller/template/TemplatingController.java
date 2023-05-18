package com.gerp.dartachalani.controller.template;

import com.gerp.dartachalani.dto.template.StandardTemplatePojo;
import com.gerp.dartachalani.service.StandardTemplateService;
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
@RequestMapping("/standard-template")
public class TemplatingController extends BaseController {
    private final StandardTemplateService standardTemplate;

    public TemplatingController(StandardTemplateService standardTemplate) {
        this.standardTemplate = standardTemplate;
        this.moduleName = "Template";
    }


    @PostMapping("/add")
    @Operation(summary = "add template", tags = {"Standard Template"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = StandardTemplatePojo.class))})
    public ResponseEntity<GlobalApiResponse> addStandardTemplate(@RequestBody StandardTemplatePojo standardTemplatePojo, BindingResult bindingResult)throws BindException{
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get(moduleName)),
                            standardTemplate.addStandardTemplate(standardTemplatePojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping
    @Operation(summary = "update template", tags = {"Standard Template"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = StandardTemplatePojo.class))})
    public ResponseEntity<GlobalApiResponse> updateStandardTemplate(@RequestBody StandardTemplatePojo standardTemplatePojo, BindingResult bindingResult)throws BindException{
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName)),
                            standardTemplate.updateStandardTemplate(standardTemplatePojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping()
    @Operation(summary = "get all template", tags = {"Standard Template"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = StandardTemplatePojo.class))})
    public ResponseEntity<GlobalApiResponse> getStandardTemplate(){
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                            standardTemplate.getStandardTemplate()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "get template by id", tags = {"Standard Template"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = StandardTemplatePojo.class))})
    public ResponseEntity<GlobalApiResponse> getStandardTemplateById(@PathVariable int id){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                        standardTemplate.getStandardTemplateById(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete template by id", tags = {"Standard Template"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = StandardTemplatePojo.class))})
    public ResponseEntity<GlobalApiResponse> deleteStandardTemplateById(@PathVariable int id){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete, customMessageSource.get(moduleName)),
                        standardTemplate.deleteStandardTemplateById(id)));
    }

}
