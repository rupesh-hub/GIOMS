package com.gerp.dartachalani.controller.dispatch;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.dartachalani.model.dispatch.DispatchLetterForward;
import com.gerp.dartachalani.service.DispatchLetterService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/dispatch-letter")
public class DispatchLetterController extends BaseController {

    private final DispatchLetterService dispatchLetterService;
    private final CustomMessageSource customMessageSource;

    public DispatchLetterController(DispatchLetterService dispatchLetterService, CustomMessageSource customMessageSource) {
        this.dispatchLetterService = dispatchLetterService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.DISPATCH_LETTER_MODULE_NAME;
        this.permissionName = PermissionConstants.DISPATCH_LETTERS + "_" + PermissionConstants.DISPATCH_LETTER;
        this.permissionApproval = PermissionConstants.APPROVAL + "_" + PermissionConstants.DISPATCH_APPROVAL;
    }

//    @PostMapping
//    public ResponseEntity<?> create(@Valid @RequestBody DispatchLetterRequestPojo dispatchLetterRequestPojo, BindingResult bindingResult) throws BindException {
//        if(!bindingResult.hasErrors()) {
//            DispatchLetter dispatchLetter = dispatchLetterService.saveLetter(dispatchLetterRequestPojo);
//
//            return ResponseEntity.ok(
//                    successResponse(
//                            customMessageSource.get("crud.create", customMessageSource.get("dispatch.letter")),
//                            dispatchLetter.getId()
//                    )
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }


    @PostMapping
    @Operation(summary = "Save chalani", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> create(@Valid @ModelAttribute DispatchLetterRequestPojo dispatchLetterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            DispatchLetter dispatchLetter = dispatchLetterService.saveLetter(dispatchLetterRequestPojo);

            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.create", customMessageSource.get("dispatch.letter")),
                            dispatchLetter.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }


    @PutMapping
    @Operation(summary = "Update chalani", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> update(@Valid @ModelAttribute DispatchLetterRequestPojo dispatchLetterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            DispatchLetter dispatchLetter = dispatchLetterService.update(dispatchLetterRequestPojo);

            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            dispatchLetter.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PutMapping(value = "/mid-update")
//    public ResponseEntity<?> contentUpdate(@Valid @RequestBody DispatchLetterRequestPojo dispatchLetterRequestPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            DispatchLetter dispatchLetter = dispatchLetterService.contentUpdate(dispatchLetterRequestPojo);
//
//            return ResponseEntity.ok(
//                    successResponse(
//                            customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
//                            dispatchLetter.getId()
//                    )
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }


    @GetMapping(value = "/draft")
    public ResponseEntity<?> getAllDraftDispatch() {
        ArrayList<DispatchLetterResponsePojo> dispatchLetterResponsePojos = dispatchLetterService.getAllDraftDispatch();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchLetterResponsePojos)
        );

    }

    @GetMapping(value = "/dispatch")
    public ResponseEntity<?> getAllDispatch() {
        ArrayList<DispatchLetterResponsePojo> dispatchLetterResponsePojos = dispatchLetterService.getAllDispatch();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchLetterResponsePojos)
        );

    }

//    @PutMapping
//    public ResponseEntity<?> update(@Valid @RequestBody DispatchLetterRequestPojo dispatchLetterRequestPojo, BindingResult bindingResult) throws BindException {
//
//        if (!bindingResult.hasErrors()) {
//            DispatchLetter dispatchLetter = dispatchLetterService.update(dispatchLetterRequestPojo);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
//                            dispatchLetter.getId())
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }

//    @GetMapping(value="{id}")
//    public ResponseEntity<?> getDispatchLetterById(@PathVariable Long  id)  {
//        DispatchLetterRequestPojo dispatchLetterRequestPojo= dispatchLetterService.getDispatchLetterById(id);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        leaveRequestLatestPojo)
//        );
//    }

    @PostMapping("/dispatch")
    public ResponseEntity<?> dispatchLetter(@Valid @RequestBody DispatchForwardRequestPojo dispatchForwardRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            DispatchLetterForward dispatchLetterForward = dispatchLetterService.dispatch(dispatchForwardRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get("letter-dispatched")),
                            dispatchLetterForward.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PostMapping("/forward")
//    public ResponseEntity<?> forwardLetter(@Valid @RequestBody DispatchReceiverRequestPojo dispatchReceiverRequestPojo, BindingResult bindingResult) throws BindException {
//        if(!bindingResult.hasErrors()) {
//            List<DispatchedLetterReceiver> dispatchedLetterReceiver = dispatchLetterService.forward(dispatchReceiverRequestPojo);
//            return ResponseEntity.ok(
//                    successResponse(
//                            customMessageSource.get("crud>create", customMessageSource.get("dispatch-letter-forward")),
//                            dispatchedLetterReceiver
//                    )
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }

    @GetMapping("/get-dispatched")
    public ResponseEntity<?> getDispatchedLetter() {
        List<DispatchedResponsePojo> dispatchedResponsePojos = dispatchLetterService.getAllDispatched();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchedResponsePojos)
        );
    }

    @GetMapping("/get-by-id/{id}")
    @Operation(summary = "Get chalani by id", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a json object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterDTO.class))})
    public ResponseEntity<?> getDispatchedLetterById(HttpServletRequest request, @PathVariable Long id, @RequestParam(name = "type", required = false) String type) {
        DispatchLetterDTO dispatchLetterResponsePojo = dispatchLetterService.getDispatchLetterById(id, type);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchLetterResponsePojo)
        );
    }

    @PostMapping("/testing")
    public ResponseEntity<?> dispatchLetterss(@Valid @RequestBody DispatchLetterRequestPojo dispatchLetterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            DispatchLetter dispatchLetter = dispatchLetterService.saveDispatch(dispatchLetterRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            dispatchLetter.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/get-all-internal-letters")
    public ResponseEntity<?> getAllInternalLetters(@RequestBody GetRowsRequest request) {
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchLetterService.getAllInternalLetters(request)
                )
        );
    }

    @GetMapping("/count-internal-letters")
    public ResponseEntity<?> countAllInternalLetters() {
        return ResponseEntity.ok(dispatchLetterService.testInternalLetterCount());
    }

    @PostMapping("/forward-within-office")
    public ResponseEntity<?> forwardWithinOffice(@Valid @RequestBody DispatchLetterReceiverInternalPojo dispatchLetterReceiverInternalPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                    dispatchLetterService.forwardDispatchLetterWithinOrganization(dispatchLetterReceiverInternalPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //@PreAuthorize("hasPermission(#this.this.permissionApproval,'approve')")
    @PostMapping("/approve")
    @Operation(summary = "Approve chalani", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> approvedispatchLetter(@Valid @RequestBody UpdateDispatchLetterPojo dispatchLetterPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.approveDispatchLetter(dispatchLetterPojo)));
    }

    @GetMapping("/forwarded")
    public ResponseEntity<?> getAllForwardedLetters() {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.getAllForwardedLetters()));
    }

//    @GetMapping("/verification-link/{id}")

//    public ResponseEntity<?> getVerificationLink(@PathVariable Long id) {
//        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                dispatchLetterService.getVerificationLink(id)));
//    }

    @GetMapping("/forwarded-detail")
    public ResponseEntity<?> getForwardedLetterDetail(@RequestParam Long dispatchLetterId) {

        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.getForwardedLetterDetail(dispatchLetterId)));
    }

    @GetMapping("/in-progress")
    public ResponseEntity<?> getInProgressDispatchLetter() {

        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.getInProgressDispatchLetters()));
    }

    @GetMapping("/finalized")
    public ResponseEntity<?> getInFinalizedDispatchLetter() {

        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.getFinalizedDispatchLetters()));
    }

    /**
     *
     */
    @PostMapping("/update-status")
    public ResponseEntity<?> updateDispatchLetter(@Valid @RequestBody UpdateDispatchLetterPojo dispatchLetterPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.updateForwardedDispatchLetter(dispatchLetterPojo)));
    }

    /**
     * This API is used to send chalani letter for review.
     *
     * @Param DispatchLetterReviewPOJO
     * @Return Long id.
     */
    @PostMapping("/send-for-review")
    @Operation(summary = "Forward for review", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> sendForReview(@Valid @RequestBody DispatchLetterReviewPojo dispatchLetterReviewPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.sendForReview(dispatchLetterReviewPojo)));
    }

    /**
     * This API is used to view dispatch letter in reviewer screen
     *
     * @Param Long logged in user
     * @Return Dispatch Letter..
     */
    @GetMapping("/review-list")
    public ResponseEntity<?> viewReviewDispatchLetter() {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.getReviewedDispatchLetters()));
    }

    /**
     * This API is used to update review for dispatched letters..
     *
     * @Param
     * @Return
     */
    @PostMapping("/update-review")
    public ResponseEntity<?> updateDispatchReview(@Valid @RequestBody UpdateDispatchLetterPojo updateDispatchLetterPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.updateForReview(updateDispatchLetterPojo)));
    }


    /**
     * This module updates review to back for review
     * Back for review should go back to initial user who created chalani
     */
    @PostMapping("/back-for-review")
    @Operation(summary = "Return chalani for edit", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> dispatchLetterBackForReview(@Valid @RequestBody UpdateDispatchLetterPojo updateDispatchLetterPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.backForReview(updateDispatchLetterPojo)));
    }

    @PostMapping(value = "/paginated")
    @Operation(summary = "Get chalani list ", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a List", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> getPaginatedData(@RequestBody GetRowsRequest paginatedRequest) {
        Page<DispatchLetterResponsePojo> page = dispatchLetterService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @PostMapping(value = "/archive-list")
    @Operation(summary = "Get archive chalani list ", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a List", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> getArchiveChalani(@RequestBody GetRowsRequest paginatedRequest) {
        Page<DispatchLetterResponsePojo> page = dispatchLetterService.getArchiveChalani(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    /**
     * url = "eg. dispatch-letter/115"
     * this api is used to soft delete the letter
     * this api does not return any data
     * only drafted letter are allowed to delete
     * only the letter creator is allowed to delete letter
     **/
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/{dispatchId}")
    @Operation(summary = "Delete chalani", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Delete dispatch letter", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> deleteDispatch(@PathVariable("dispatchId") Long dispatchId) {
        dispatchLetterService.deleteDispatchedLetter(dispatchId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchId)
        );
    }

    /**
     * url = "eg. dispatch-letter/archive/115"
     * this api is used to archive the letter
     * this api does not return any data
     * only reverted letter are allowed to archive
     * only the letter creator is allowed to archive letter
     **/
    @DeleteMapping("/archive/{dispatchId}")
    @Operation(summary = "Archive chalani", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Archive dispatch letter", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> archiveDispatchLetter(@PathVariable("dispatchId") Long dispatchId) {
        dispatchLetterService.archiveDispatchedLetter(dispatchId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.restore", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchId)
        );
    }

     /**
      * url = "eg. dispatch-letter/115"
      * this api is used to restore the archived letter
      * this api does not return any data
      **/
    @PutMapping("/{dispatchId}")
    @Operation(summary = "Restore chalani", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Restore dispatch letter", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<?> restoreDispatch(@PathVariable("dispatchId") Long dispatchId) {
        dispatchLetterService.restoreDispatchedLetter(dispatchId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.restore", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchId)
        );
    }

    @GetMapping("/external-list")
    public ResponseEntity<?> getExternalUsers() {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.getExternalUsers()));
    }

    @PostMapping("/save-pdf")
    public ResponseEntity<?> savePdf(@Valid @RequestBody SaveDispatchPdfPojo data) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.save", customMessageSource.get(moduleName.toLowerCase())),
                dispatchLetterService.savePdf(data)));
    }

    @PutMapping(value = "/imp")
    @Operation(summary = "Set important flag for chalani user", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchLetterResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> setImportantFlag(@RequestParam Long id, @RequestParam boolean value, @RequestParam(required = false) String type) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                        dispatchLetterService.setImportantFlag(id, value, type))
        );
    }

    /***
     * Get search recommendation for chalani view
     * @return
     */
    @GetMapping("/search-recommendation")
    public ResponseEntity<GlobalApiResponse> getSearchRecomendation() {
        return ResponseEntity.ok((successResponse(customMessageSource.get("success.retrieve"), dispatchLetterService.getSearchRecommendation())));
    }

    @GetMapping("/internal-letter-search-recommendation")
    public ResponseEntity<GlobalApiResponse> getSearchRecomendationInternalLetter() {
        return ResponseEntity.ok((successResponse(customMessageSource.get("success.retrieve"), dispatchLetterService.getInternalLetterSender())));
    }
}
