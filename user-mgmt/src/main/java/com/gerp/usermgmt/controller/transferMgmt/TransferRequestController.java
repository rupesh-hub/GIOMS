package com.gerp.usermgmt.controller.transferMgmt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.transfer.TransferRequestForOfficePojo;
import com.gerp.usermgmt.pojo.transfer.TransferRequestPojo;
import com.gerp.usermgmt.pojo.transfer.TransferRequestToTransferPojo;
import com.gerp.usermgmt.pojo.transfer.TransferResponsePojo;
import com.gerp.usermgmt.services.transfer.TransferRequestService;
import com.gerp.usermgmt.services.transfer.impl.SaruwaService;
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
import java.util.List;

@RestController
@RequestMapping("/transfer-request")
public class TransferRequestController extends BaseController {

    private final TransferRequestService transferRequestService;
    private final SaruwaService saruwaService;

    public TransferRequestController(TransferRequestService transferRequestService, SaruwaService saruwaService) {
        this.transferRequestService = transferRequestService;
        this.saruwaService = saruwaService;
        this.moduleName = PermissionConstants.TRANSFER_REQUEST;
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "use for adding  transfer request by employee", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponsePojo.class))})
    public ResponseEntity<?> addTransferRequest(@Valid @ModelAttribute TransferRequestPojo transferRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.add, customMessageSource.get(moduleName)),
                            transferRequestService.addTransferRequest(transferRequestPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/transfer")
    @Operation(summary = "use for adding  transfer request to transfer tippadi list", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponsePojo.class))})
    public ResponseEntity<?> addTransferRequestToTransferList(@Valid @RequestBody  List<TransferRequestToTransferPojo> transferRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create, customMessageSource.get(moduleName)),
                            transferRequestService.addTransferRequestToTransfer(transferRequestPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee transfer request detail by id", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponsePojo.class))})
    public ResponseEntity<?> getTransferRequest( @PathVariable Long id) {
       List<TransferResponsePojo> transferRequestPojo =  transferRequestService.getTransferRequest(id);
        return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), transferRequestPojo));
    }

//    @GetMapping("/{id}")
//    @Operation(summary = "Get employee transfer request", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponsePojo.class))})
//    public ResponseEntity<?> getTransferRequestMin(@PathVariable Long id) {
//        List<TransferResponsePojo> transferRequestPojo =  transferRequestService.getTransferRequestDetailMini(id);
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), transferRequestPojo));
//    }

    @GetMapping("/list")
    @Operation(summary = "Get employee self Created transfer request", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponsePojo.class))})
    public ResponseEntity<?> getTransferRequest() {
        List<TransferResponsePojo> transferRequestPojo =  transferRequestService.getTransferSelfCreated();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), transferRequestPojo));
    }

    @GetMapping("/office")
    @Operation(summary = "Get employee transfer request list by the requested office employee ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferRequestForOfficePojo.class))})
    public ResponseEntity<?> getTransferRequestToOffice( @RequestParam(required = false,defaultValue = "10") int limit,
                                                        @RequestParam(required = false,defaultValue = "1") int page) {
        Page<TransferRequestForOfficePojo> transferRequestPojo =  transferRequestService.getTransferRequestToOffice(limit,page);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), transferRequestPojo));
    }

    @GetMapping("/generate-saruwa/{id}")
    @Operation(summary = "generate saruwa request letter", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> generateSaruwa(@PathVariable Long id) {
       Long document =  saruwaService.generateSaruwa(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("generate"), document));
    }


}
