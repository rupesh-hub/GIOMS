package com.gerp.dartachalani.controller.initial;

import com.gerp.dartachalani.dto.DCNumberPojo;
import com.gerp.dartachalani.dto.FooterDataDto;
import com.gerp.dartachalani.service.FooterDataService;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/footer")
public class FooterDataController extends BaseController {

    private final FooterDataService footerDataService;

    private static final String FOOTER = "Footer";

    public FooterDataController(FooterDataService footerDataService) {
        this.footerDataService = footerDataService;
    }

    @PostMapping
    @Operation(summary = "Save footer data for office", tags = {"footer"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FooterDataDto.class))})
    public ResponseEntity<GlobalApiResponse> create(@RequestBody FooterDataDto data) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.create", FOOTER), footerDataService.saveFooter(data))
        );
    }

    @PutMapping
    @Operation(summary = "Update footer data for office", tags = {"footer"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FooterDataDto.class))})
    public ResponseEntity<GlobalApiResponse> update(@RequestBody FooterDataDto data) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", FOOTER), footerDataService.updateFooter(data))
        );
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get footer data by id and language", tags = {"footer"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FooterDataDto.class))})
    public ResponseEntity<GlobalApiResponse> getById(@PathVariable("id") Long id, @RequestParam(name = "lang", required = false) String lang) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", FOOTER),footerDataService.getById(id, lang))
        );
    }

    @GetMapping
    @Operation(summary = "Get footer data by office code", tags = {"footer"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a json", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FooterDataDto.class))})
    public ResponseEntity<GlobalApiResponse> getByOfficeCOde() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", FOOTER),footerDataService.getByOfficeCode())
        );
    }

    @DeleteMapping(value = "/toggle/{id}")
    @Operation(summary = "Toggle active/inactive footer data for office", tags = {"footer"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FooterDataDto.class))})
    public ResponseEntity<GlobalApiResponse> toggleActive(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.status", "Template"), footerDataService.toggleActive(id))
        );
    }

}
