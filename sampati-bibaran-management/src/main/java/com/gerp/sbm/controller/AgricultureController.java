//package com.gerp.sbm.controller;
//
//import com.gerp.sbm.pojo.RequestPojo.AgricultureDetailRequestPojo;
//import com.gerp.sbm.pojo.ResponsePojo.AgricultureDetailResponsePojo;
//import com.gerp.sbm.service.AgricultureService;
//import com.gerp.shared.generic.controllers.BaseController;
//import com.gerp.shared.utils.CrudMessages;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindException;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping("/agriculture")
//public class AgricultureController extends BaseController {
//
//    private final AgricultureService agricultureService;
//
//    public AgricultureController(AgricultureService agricultureService) {
//        this.agricultureService = agricultureService;
//    }
//
//    @PostMapping()
//    @Operation(summary = "Add a agriculture related details", tags = {"AGRICULTURE"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AgricultureDetailResponsePojo.class))})
//    public ResponseEntity<?> addAgroDetails(@Valid @RequestBody AgricultureDetailRequestPojo agricultureDetailRequestPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            Long id = agricultureService.addAgriculture(agricultureDetailRequestPojo);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.create,
//                            customMessageSource.get(moduleName.toLowerCase())),
//                            id));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @PutMapping()
//    @Operation(summary = "Update a agriculture related details", tags = {"AGRICULTURE"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AgricultureDetailResponsePojo.class))})
//    public ResponseEntity<?> updateAgroDetails(@Valid @RequestBody AgricultureDetailRequestPojo agricultureDetailRequestPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            Long id = agricultureService.updateAgriculture(agricultureDetailRequestPojo);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.create,
//                            customMessageSource.get(moduleName.toLowerCase())),
//                            id));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }
//}
