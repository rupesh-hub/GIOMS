package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.pojo.organization.office.ExternalOfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeGroupPojo;
import com.gerp.usermgmt.services.organization.office.OfficeGroupingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/office-group")
@RestController
public class GroupingOfficesController extends BaseController {
    private final OfficeGroupingService officeGroupingService;
//    private final ExternalOfficeService externalOfficeService;

    public GroupingOfficesController(OfficeGroupingService officeGroupingService) {
        this.officeGroupingService = officeGroupingService;
        this.moduleName = "Office";
    }

//    @PostMapping("/other")
//    @Operation(summary = "call for the adding other office..", tags = {"External office"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExternalOfficePojo.class))})
//    public ResponseEntity<GlobalApiResponse> addExternalOffice(@Valid @RequestBody ExternalOfficePojo externalOfficePojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.add, customMessageSource.get(moduleName.toLowerCase())),
//                            externalOfficeService.addExternalOffice(externalOfficePojo)));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @PutMapping("/other")
//    @Operation(summary = "call for the adding other office..", tags = {"External office"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExternalOfficePojo.class))})
//    public ResponseEntity<GlobalApiResponse> updateExternalOffice(@Valid @RequestBody ExternalOfficePojo externalOfficePojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName.toLowerCase())),
//                            externalOfficeService.updateExternalOffice(externalOfficePojo)));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }
//    @GetMapping("/other")
//    @Operation(summary = "call for the get external Office..", tags = {"External office"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OfficeGroupPojo.class))})
//    public ResponseEntity<GlobalApiResponse> getOfficeGroup(){
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName.toLowerCase())),
//                        externalOfficeService.getExternalOffice()));
//    }

    @PostMapping
    @Operation(summary = "call for the grouping office..", tags = {"Grouping"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OfficeGroupPojo.class))})
    public ResponseEntity<GlobalApiResponse> addOfficeGroup(@Valid @RequestBody OfficeGroupPojo officeGroupPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add, customMessageSource.get(moduleName.toLowerCase())),
                          officeGroupingService.
                                  addOfficeGroup(officeGroupPojo)));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/external-office")
    @Operation(summary = "call for the external office..", tags = {"Grouping"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OfficeGroupPojo.class))})
    public ResponseEntity<GlobalApiResponse> addExternalOfficeGroup(@Valid @RequestBody ExternalOfficePojo officeGroupPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add, customMessageSource.get(moduleName.toLowerCase())),
                            officeGroupingService.addExternalOfficeGroup(officeGroupPojo)));
        }else {
            throw new BindException(bindingResult);
        }
    }
    @PutMapping
    @Operation(summary = "call for the updating grouping office..", tags = {"Grouping"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OfficeGroupPojo.class))})
    public ResponseEntity<GlobalApiResponse> updateOfficeGroup(@Valid @RequestBody OfficeGroupPojo officeGroupPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName.toLowerCase())),
                            officeGroupingService.updateOfficeGroup(officeGroupPojo)));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "call for the getting grouping office..", tags = {"Grouping"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OfficeGroupPojo.class))})
    public ResponseEntity<GlobalApiResponse> getOfficeGroup(@RequestParam(required = false) String officeCode, @RequestParam(required = false) String districtCode,@RequestParam(required = false) String officeLevelCode){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName.toLowerCase())),
                        officeGroupingService.getOfficeGroup(officeCode,districtCode,officeLevelCode)));
    }

    @GetMapping("/external-office")
    @Operation(summary = "call for the getting external office..", tags = {"Grouping"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OfficeGroupPojo.class))})
    public ResponseEntity<GlobalApiResponse> getExternalOfficeGroup(){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName.toLowerCase())),
                        officeGroupingService.getExternalOfficeGroup()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "call for the deleting grouping office..", tags = {"Grouping"}, security = {@SecurityRequirement(name = "bearer-key")})
   public ResponseEntity<GlobalApiResponse> deleteOfficeGroup(@PathVariable Integer id){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete, customMessageSource.get(moduleName.toLowerCase())),
                        officeGroupingService.deleteOfficeGroup(id)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "call for the getting grouping office..", tags = {"Grouping"}, security = {@SecurityRequirement(name = "bearer-key")})
   public ResponseEntity<GlobalApiResponse> getOfficeGroupId(@PathVariable Integer id){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName.toLowerCase())),
                        officeGroupingService.getOfficeGroup(id)));
    }

}
