package com.gerp.usermgmt.controller.orgtransfer;


import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;
import com.gerp.usermgmt.services.orgtransfer.OrgTransferRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/org-transfer-request")
public class OrgTransferRequestController extends BaseController {
    private final OrgTransferRequestService orgTransferRequestService;


    public OrgTransferRequestController(OrgTransferRequestService orgTransferRequestService) {
        this.orgTransferRequestService = orgTransferRequestService;
         this.moduleName= PermissionConstants.TRANSFER;
    }


    @PostMapping
    @Operation(summary = "Request Transfer", tags = {"TRANSFER Request"})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> requestTransfer(@Valid @RequestBody OrgTransferRequestPojo orgTransferRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get(moduleName)),
                            orgTransferRequestService.requestTransfer(orgTransferRequestPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/action")
    @Operation(summary = "Transfer Action", tags = {"Transfer Action"})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> transferAction(@Valid @RequestBody OrgTransferRequestPojo orgTransferRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName)),
                            orgTransferRequestService.changeTransferAction(orgTransferRequestPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/office-transfer-list")
    @Operation(summary = "list of transfer list ", tags = {"TRANSFER MANAGEMENT"})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> getCurrentOfficeTransfer(@RequestBody GetRowsRequest paginatedRequest, BindingResult bindingResult) throws BindException{

        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                            orgTransferRequestService.requestedTransferPaginated(paginatedRequest)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/transfer-list")
    @Operation(summary = "list of transfer list ", tags = {"TRANSFER MANAGEMENT"})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> getDynamicTransferList(@RequestBody GetRowsRequest paginatedRequest, BindingResult bindingResult) throws BindException{

        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
            successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                    orgTransferRequestService.allFilteredTransferPaginated(paginatedRequest)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "transfer request detail ", tags = {"TRANSFER MANAGEMENT"})
    @ApiResponse(responseCode = "200", description = "Returns a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> transferDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                        orgTransferRequestService.transferDetail(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "acknowledge transfer ", tags = {"TRANSFER MANAGEMENT"})
    @ApiResponse(responseCode = "200", description = "Returns a id")
    public ResponseEntity<?> acknowledgeTransfer(@PathVariable("id") Long id,@RequestParam(value = "deviceId", required = false) Long deviceId){
        orgTransferRequestService.acknowledgeTransfer(id,deviceId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName)),
                       null));
    }

    @PutMapping()
    @Operation(summary = "Update Transfer Request", tags = {"TRANSFER MANAGEMENT"})
    @ApiResponse(responseCode = "200", description = "Returns a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> updateTransferRequest(@RequestBody OrgTransferRequestPojo orgTransferRequest ,BindingResult bindingResult) throws BindException{
        if (!bindingResult.hasErrors()) {
         orgTransferRequestService.update(orgTransferRequest);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName)),
                            null));
        } else {
            throw new BindException(bindingResult);
        }
    }

}
