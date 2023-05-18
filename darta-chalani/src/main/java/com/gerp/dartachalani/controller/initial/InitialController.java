package com.gerp.dartachalani.controller.initial;

import com.gerp.dartachalani.config.exception.CustomException;
import com.gerp.dartachalani.config.exception.CustomResponse;
import com.gerp.dartachalani.dto.DCNumberPojo;
import com.gerp.dartachalani.dto.InitialPojo;
import com.gerp.dartachalani.dto.ReceivedLetterResponsePojo;
import com.gerp.dartachalani.service.InitialService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/initial")
public class InitialController extends BaseController {

    private final InitialService initialService;
    private final CustomMessageSource customMessageSource;

    public InitialController(InitialService initialService,
                             CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
        this.initialService = initialService;
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "Set darta and chalani no", tags = {"initial"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DCNumberPojo.class))})
    public ResponseEntity<?> create(@Valid @RequestBody DCNumberPojo data){

        try {
            initialService.save(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", "Initial"),
                            "Initial")
            );
        } catch (CustomException e) {
           return ResponseEntity.badRequest().body(new CustomResponse(null, e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @GetMapping("/darta/{officeCode}")
    @Operation(summary = "Get darta no by office code", tags = {"initial"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a string", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DCNumberPojo.class))})
    public ResponseEntity<?> getDartaNo(@PathVariable("officeCode") String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.create", "Darta Number"), initialService.getDartaNumber(officeCode))
        );
    }

    @GetMapping("/chalani/{sectionCode}")
    @Operation(summary = "Get chalani no by office code", tags = {"initial"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a string", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DCNumberPojo.class))})
    public ResponseEntity<?> getChalaniNo(@PathVariable("sectionCode") String sectionCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.create", "Chalani Number"), initialService.getChalaniNumber(sectionCode))
        );
    }

    @GetMapping("/{officeCode}")
    @Operation(summary = "Get darta and chalani no by office code", tags = {"initial"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a json", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = InitialPojo.class))})
    public ResponseEntity<?> getByOfficeCode(@PathVariable("officeCode") String officeCode) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", "Chalani/Darta Number"), initialService.getByOfficeCode(officeCode))
        );
    }

}
