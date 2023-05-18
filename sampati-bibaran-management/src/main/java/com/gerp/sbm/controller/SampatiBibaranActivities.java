package com.gerp.sbm.controller;


import com.gerp.sbm.pojo.RequestPojo.FixedAssetRequestPojo;
import com.gerp.sbm.service.SampatiBibaranActivitiesService;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.netflix.discovery.converters.Auto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/property_info")
public class SampatiBibaranActivities extends BaseController {

    @Autowired
    private SampatiBibaranActivitiesService sampatiBibaranActivitiesService;

    @PostMapping("/review_request")
    @Operation(summary = "Add a REVIEW_REQUEST related details", tags = {"REVIEW_REQUEST"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addFixedAsset(@RequestParam String piscode) {
        if (piscode!=null) {
            sampatiBibaranActivitiesService.addrequest(piscode);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "REVIEW_REQUEST"),null));

        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"piscode must be valid.");
        }
    }

    @GetMapping("/review_request")
    @Operation(summary = "Add a REVIEW_REQUEST related details", tags = {"REVIEW_REQUEST"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> getReviewRequest() {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "REVIEW_REQUEST"),sampatiBibaranActivitiesService.getReviewRequest()));
    }

    @GetMapping("/review_approved")
    @Operation(summary = "Add a REVIEW_REQUEST_APPROVED related details", tags = {"REVIEW_REQUEST_APPROVED"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> approvedRequest(@RequestParam String requester_piscode) {
        if(requester_piscode!=null){
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "REVIEW_REQUEST_APPROVED"),sampatiBibaranActivitiesService.approvedRequest(requester_piscode)));
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"requester_piscode must be valid.");
        }

    }
    //notif

    @GetMapping("/details")
    @Operation(summary = "Get a Sampati Bibaran related details", tags = {"Sampati Bibaran"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> getSampatiBibaran(@RequestParam String piscode,@RequestParam String fiscal_year_code) {
        if(piscode!=null && fiscal_year_code!=null){
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "Sampati Bibaran"),sampatiBibaranActivitiesService.getSampatiBibaran(piscode,fiscal_year_code)));
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"requester_piscode must be valid.");
        }

    }
}
