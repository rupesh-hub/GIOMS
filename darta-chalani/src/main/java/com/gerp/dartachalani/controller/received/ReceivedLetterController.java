package com.gerp.dartachalani.controller.received;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.model.receive.ReceivedLetter;
import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import com.gerp.dartachalani.service.ReceivedLetterService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/received-letter")
public class ReceivedLetterController extends BaseController {

    private final ReceivedLetterService receivedLetterService;
    private final CustomMessageSource customMessageSource;

    public ReceivedLetterController(ReceivedLetterService receivedLetterService, CustomMessageSource customMessageSource) {
        this.receivedLetterService = receivedLetterService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.RECEIVED_LETTER_MODULE_NAME;
        this.permissionName = PermissionConstants.RECEIVED_LETTERS + "_" + PermissionConstants.RECEIVED_LETTER;
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ReceivedLetterRequestPojo receivedLetterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            ReceivedLetter receivedLetter = receivedLetterService.saveReceivedLetter(null);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            receivedLetter.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/manual/save")
    @Operation(summary = "Save manual darta", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ManualReceivedRequestPojo.class))})
    public ResponseEntity<?> saveManual(@Valid @ModelAttribute ManualReceivedRequestPojo manualReceivedRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            ReceivedLetter receivedLetter = receivedLetterService.saveManual(manualReceivedRequestPojo);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            receivedLetter.getId()
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * this api is used to update received letter which is created manually
    **/

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping("/manual/update")
    @Operation(summary = "Update darta", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ManualReceivedRequestPojo.class))})
    public ResponseEntity<?> updateManuallyReceivedLetter(@Valid @ModelAttribute ManualReceivedRequestPojo manualReceivedRequestPojo) {
        receivedLetterService.updateManuallyReceivedLetter(manualReceivedRequestPojo);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                        manualReceivedRequestPojo.getId())
        );
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @PostMapping("/manual/forward")
    @Operation(summary = "Office Head: Forward darta", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterForwardRequestPojo.class))})
    public ResponseEntity<?> forwardManual(@Valid @RequestBody ReceivedLetterForwardRequestPojo receivedLetterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            if (receivedLetterRequestPojo.getIsMultipleForwards()) {
                List<Long> ids = new ArrayList<>();
                List<Long> failureIds = new ArrayList<>();
                Map<String, Object> responseBody = new HashMap<>();
                if (receivedLetterRequestPojo.getReceivedLetterIds() != null && !receivedLetterRequestPojo.getReceivedLetterIds().isEmpty()) {
                    receivedLetterRequestPojo.getReceivedLetterIds().stream().forEach(obj -> {
                        receivedLetterRequestPojo.setReceivedLetterId(obj);
                        if (receivedLetterService.forwardManualLetter(receivedLetterRequestPojo, true)) {
                            ids.add(obj);
                        } else {
                            failureIds.add(obj);
                        }
                    });
                }

                responseBody.put("successIds", ids);
                responseBody.put("failureIds", failureIds);
                Boolean isAllSuccess = ids.size() == receivedLetterRequestPojo.getReceivedLetterIds().size() ? true : false;
                Boolean isAllFailure = failureIds.size() == receivedLetterRequestPojo.getReceivedLetterIds().size() ? true : false;
                responseBody.put("isAllSuccess", isAllSuccess);
                responseBody.put("isAllFailure", isAllFailure);
                return ResponseEntity.ok(
                        successResponse(
                                customMessageSource.get("crud.forwardMul", isAllSuccess ? "All" : isAllFailure ? "No" : "Some", customMessageSource.get(moduleName.toLowerCase()), isAllFailure ? "" : "Successfully"),
                                responseBody
                        )
                );
            } else {
                Long receivedLetterForwards = receivedLetterService.forwardManualLetter(receivedLetterRequestPojo, false) ? receivedLetterRequestPojo.getReceivedLetterId() : null;
                return ResponseEntity.ok(
                        successResponse(
                                customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                                receivedLetterForwards
                        )
                );
            }
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/forward")
    @Operation(summary = "Regular User: Forward darta", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterForwardRequestPojo.class))})
    public ResponseEntity<?> forwardReceived(@Valid @RequestBody ReceivedLetterForwardRequestPojo receivedLetterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            if (receivedLetterRequestPojo.getIsMultipleForwards()) {
                List<Long> ids = new ArrayList<>();
                List<Long> failureIds = new ArrayList<>();
                Map<String, Object> responseBody = new HashMap<>();
                if (receivedLetterRequestPojo.getReceivedLetterIds() != null && !receivedLetterRequestPojo.getReceivedLetterIds().isEmpty()) {
                    receivedLetterRequestPojo.getReceivedLetterIds().stream().forEach(obj -> {
                        receivedLetterRequestPojo.setReceivedLetterId(obj);
                        if (receivedLetterService.forwardReceivedLetter(receivedLetterRequestPojo, true)) {
                            ids.add(obj);
                        } else {
                            failureIds.add(obj);
                        }
                    });
                }

                responseBody.put("successIds", ids);
                responseBody.put("failureIds", failureIds);
                Boolean isAllSuccess = ids.size() == receivedLetterRequestPojo.getReceivedLetterIds().size() ? true : false;
                Boolean isAllFailure = failureIds.size() == receivedLetterRequestPojo.getReceivedLetterIds().size() ? true : false;
                responseBody.put("isAllSuccess", isAllSuccess);
                responseBody.put("isAllFailure", isAllFailure);
                return ResponseEntity.ok(
                        successResponse(
                                customMessageSource.get("crud.forwardMul", isAllSuccess ? "All" : isAllFailure ? "No" : "Some", customMessageSource.get(moduleName.toLowerCase()), isAllFailure ? "" : "Successfully"),
                                responseBody
                        )
                );
            } else {
                Long receivedLetterForwards = receivedLetterService.forwardReceivedLetter(receivedLetterRequestPojo, false) ? receivedLetterRequestPojo.getReceivedLetterId() : null;

                return ResponseEntity.ok(
                        successResponse(
                                customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                                receivedLetterForwards
                        )
                );
            }

        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/revert")
    public ResponseEntity<GlobalApiResponse> revertReceivedLetter(@RequestParam Long receivedLetterId, @RequestParam String description) {
        ReceivedLetterForward receivedLetterForwards = receivedLetterService.revertReceivedLetter(receivedLetterId, description);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.backward.darta"),
                        receivedLetterForwards
                )
        );
    }

    @PostMapping("/forward/cc")
    @Operation(summary = "Office Head: Forward darta as cc", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterForward.class))})
    public ResponseEntity<?> forwardReceivedCc(@Valid @RequestBody ReceivedLetterForwardRequestPojo receivedLetterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            List<ReceivedLetterForward> receivedLetterForwards = receivedLetterService.forwardReceivedLetterCc(receivedLetterRequestPojo);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                            receivedLetterForwards
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<?> comment(@Valid @RequestBody ReceivedLetterMessageRequestPojo receivedLetterMessageRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = receivedLetterService.saveMessage(receivedLetterMessageRequestPojo);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.create", customMessageSource.get("received.message")),
                            id
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping("/forward/status")
    public ResponseEntity<?> updateStatus(@RequestBody StatusPojo statusPojo) {
        receivedLetterService.updateStatus(statusPojo);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.status", customMessageSource.get(moduleName.toLowerCase())),
                        statusPojo.getId()
                )
        );
    }

    @PostMapping("/id")
    @Operation(summary = "Get darta By id", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<?> getManuallyReceivedLetter(@RequestBody GetRowsRequest request) {
        ReceivedLetterResponsePojo receivedLetter = receivedLetterService.getReceivedLetter(request.getId(), request);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetter)
        );
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionReport,'read')")
    @GetMapping("/manual/get-all")
    public ResponseEntity<?> getAllManuallyReceivedLetter() {
        List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getAllManuallyReceivedLetter();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos)
        );
    }

    @GetMapping("/get-letter-users/{rlId}")
    public ResponseEntity<?> getAllActiveReceivedLetterUsers(@PathVariable(name = "rlId") Long id) {
        List<ForwardResponsePojo> forwardResponsePojos = receivedLetterService.getCurrentReceivedLetterOwners(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        forwardResponsePojos)
        );
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping("/manual/head/get-all")
    public ResponseEntity<?> getAllManualOfficeHead() {
        List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getAllManualOfficeHead();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos)
        );
    }

    @GetMapping("/manual/forwards")
    public ResponseEntity<?> getAllManualLetterForward() {
        List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getAllManualLettersForward();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos)
        );
    }

    @GetMapping("/received")
    public ResponseEntity<?> getAllByReceiverPisCode() {
        List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getAllReceivedLetter();
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos
                )
        );
    }

    @PostMapping("/forwards")
    public ResponseEntity<?> getAllReceivedLetterForward(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getAllReceivedLettersForward(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos)
        );
    }

    @PostMapping("/rec/in-progress")
    @Operation(summary = "Get darta in progress", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<?> getManualReceiverInProgress(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getManualReceiverInProgress(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos)
        );
    }

    @PostMapping("/for-transfer")
    @Operation(summary = "Get darta for transfer", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<?> getReceivedLetterForTransfer(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getReceivedLetterForTransfer(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos)
        );
    }

    @PostMapping("/rec/finalized")
    @Operation(summary = "Get darta in progress", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<?> getManualReceiverFinalized(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> receivedLetterResponsePojos = receivedLetterService.getManualReceiverFinalized(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterResponsePojos)
        );
    }

    // TODO: 7/8/21 Delete for draft 
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
//    @DeleteMapping("/{letterId}")
//    public ResponseEntity<?> deleteReceivedLetter(@PathVariable("letterId") Long letterId) {
//        receivedLetterService.deleteReceivedLetter(letterId);
//        return ResponseEntity.ok(
//                successResponse(
//                        customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
//                        letterId
//                )
//        );
//    }

    @PostMapping(value = "/paginated")
    @Operation(summary = "Regular user: Get darta list according to payload", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<?> getPaginatedData(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> page = receivedLetterService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    // not required as of now
//    @PostMapping(value = "/archive-list")
//    @Operation(summary = "Regular user: Get archive darta list according to payload", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
//    public ResponseEntity<?> getArchiveDarta(@RequestBody GetRowsRequest paginatedRequest) {
//        Page<ReceivedLetterResponsePojo> page = receivedLetterService.getArchiveDarta(paginatedRequest);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
//                        page)
//        );
//    }

    @PostMapping(value = "/page")
    @Operation(summary = "Office Head/Darta User : Get darta list according to payload", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<?> getPageData(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> page = receivedLetterService.pageData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @PutMapping(value = "/imp")
    @Operation(summary = "Set important flag for darta user", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a Id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> setImportantFlag(@RequestParam Long id, @RequestParam boolean value) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                        receivedLetterService.setImportantFlag(id, value))
        );
    }

    /***
     * Get search recommendation for darta view
     * @return
     */
    @GetMapping("/darta-search-recommendation")
    public ResponseEntity<GlobalApiResponse> getDartaSearchRecommendation() {
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), receivedLetterService.getDartaSearchRecommendation()));
    }

    /***
     * check section is involved to create darta , chalani or tippani
     * @return
     */
    @GetMapping("/check/section")
    public ResponseEntity<GlobalApiResponse> checkSectionIsInvolved(@RequestParam("sectionCode") String sectionCode) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), receivedLetterService.checkSectionIsInvolved(sectionCode)));
    }

}
