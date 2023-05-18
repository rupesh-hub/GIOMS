package com.gerp.kasamu.controller.kasamu;

import com.gerp.kasamu.constant.CurdMessages;
import com.gerp.kasamu.constant.PermissionConstants;
import com.gerp.kasamu.pojo.request.KasamuMasterRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuReviewTopicsRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuReviewTopicsResponsePojo;
import com.gerp.kasamu.service.ReviewTopicService;
import com.gerp.shared.generic.controllers.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/topic")
public class ReviewTopicController extends BaseController {

    private final ReviewTopicService reviewTopicService;

    public ReviewTopicController(ReviewTopicService reviewTopicService) {
        this.reviewTopicService = reviewTopicService;
        this.moduleName = PermissionConstants.TOPIC;
    }

    @PostMapping
    @Operation(summary = "Add a new review topic", tags = {"TOPICS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuReviewTopicsResponsePojo.class))})
    public ResponseEntity<?> addReviewTopic(@Valid @RequestBody KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = reviewTopicService.addReviewTopic(kasamuMasterRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping
    @Operation(summary = "Update a  review topic", tags = {"TOPICS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuReviewTopicsResponsePojo.class))})
    public ResponseEntity<?> updateReviewTopic(@Valid @RequestBody KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = reviewTopicService.updateReviewTopic(kasamuMasterRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "get review topic according to the class and reviewType", tags = {"TOPICS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuReviewTopicsResponsePojo.class))})
    public ResponseEntity<?> getReviewTopic(@RequestParam String pisCode,@RequestParam(required = false, defaultValue = "COMMITTEE") String reviewType) {

            List<KasamuReviewTopicsResponsePojo> reviewTopic = reviewTopicService.getReviewTopic(pisCode,reviewType);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.get,
                            customMessageSource.get(moduleName.toLowerCase())),
                            reviewTopic));
    }

    @GetMapping("/{id}")
    @Operation(summary = "get review topic by id", tags = {"TOPICS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuReviewTopicsResponsePojo.class))})
    public ResponseEntity<?> getReviewTopicById(@PathVariable Long id) {

        KasamuReviewTopicsResponsePojo reviewTopic = reviewTopicService.getReviewTopicById(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CurdMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        reviewTopic));
    }
}
