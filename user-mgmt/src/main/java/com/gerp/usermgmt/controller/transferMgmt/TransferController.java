package com.gerp.usermgmt.controller.transferMgmt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.constant.TransferConstant;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.transfer.*;
import com.gerp.usermgmt.services.transfer.TransferService;
import com.gerp.usermgmt.services.transfer.impl.SaruwaService;
import com.google.gson.internal.$Gson$Preconditions;
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
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/transfer")
public class TransferController extends BaseController {

    private final TransferService transferService;
    private final SaruwaService saruwaService;

    public TransferController(TransferService transferService, SaruwaService saruwaService) {
        this.transferService = transferService;
        this.saruwaService = saruwaService;
        this.moduleName= PermissionConstants.TRANSFER;
        this.permissionName= PermissionConstants.TRANSFEREMPLOYEE + "_"+ PermissionConstants.TRANSFEREMPLOYEE;
        this.permissionName2 = PermissionConstants.SARUWACONFIGURATION+"_"+PermissionConstants.SARUWACONFIGURATION;
        this.permissionApproval = PermissionConstants.TRANSFEREMPLOYEE+"_"+PermissionConstants.TRANSFERTIPADI;
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "add transfer of the employee from one office to another", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> addTransfer(@Valid @ModelAttribute("transferPojo") TransferModelPojo transferPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get(moduleName)),
                            transferService.addTransfer(transferPojo.getTransferRequest())));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping
    @Operation(summary = "update transfer of the employee from one office to another", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> editTransfer(@Valid @ModelAttribute("transferPojo") TransferPojo transferPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get(moduleName)),
                            transferService.updateTransfer(transferPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

        //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/transfer-list")
    @Operation(summary = " transfer to be selected and create tippadi ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferSubmissionResponsePojo.class))})
    public ResponseEntity<?> getTransferListForTippadi(@RequestParam( required = false) Boolean withIn,
                                                       @RequestParam(required = false) String searchKey,
                                                       @RequestParam(required = false,defaultValue = "Pending") String status,
                                                       @RequestParam(required = false,defaultValue = "10") int limit,
                                                       @RequestParam(required = false,defaultValue = "1") int page ) {
        Page<TransferSubmissionResponsePojo> transfers =  transferService.getTransferListForTippadi(withIn,searchKey,limit,page,status);
            return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transfers ));
    }

    @DeleteMapping("/transfer/{id}")
    @Operation(summary = " delete transfer by id  ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferSubmissionResponsePojo.class))})
    public ResponseEntity<?> deleteTransfer(@PathVariable Long id) {
       transferService.deleteTransfer(id);
        return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.delete, customMessageSource.get(moduleName)),id ));
    }

//    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
//    @GetMapping("/get-transfer/{isForEmployee}")
//    @Operation(summary = "get transfer to be decided ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferSubmissionResponsePojo.class))})
//    public ResponseEntity<?> getTransferToBeDecided(@PathVariable Boolean isForEmployee) {
//        List<TransferSubmissionResponsePojo>  transfers =  transferService.getTransferToBeDecided(isForEmployee);
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transfers ));
//    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/rawana-list")
    @Operation(summary = "get rawana to be decided ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferSubmissionResponsePojo.class))})
    public ResponseEntity<?> getRawanaList(@RequestParam(required = false) String status) {
        List<TransferSubmissionResponsePojo>  transfers =  transferService.getRawanaList(status);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transfers ));
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/get-transfer-id")
    @Operation(summary = "get transfer id ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferSubmissionResponsePojo.class))})
    public ResponseEntity<?> getTransferById(@RequestParam Long id, @RequestParam String type) {
     List<TransferSubmissionResponsePojo>  transfers =  transferService.getTransferBy(id,type.toUpperCase());
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transfers ));
    }
//
//    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
//    @PutMapping("joining-date")
//    @Operation(summary = "add joining date", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
//    public ResponseEntity<?> addJoiningDate(@Valid @RequestBody JoiningDatePojo joiningDatePojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get(moduleName)),
//                            transferService.updateJoiningDate(joiningDatePojo)));
//        } else {
//            throw new BindException(bindingResult);
//        }
//
//    }

//    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
//    @PutMapping("/decision")
//    @Operation(summary = " decide on transfer request ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
//    public ResponseEntity<?> updateTransferDecision(@Valid @RequestBody TransferDecisionPojo transferDecisionPojo , BindingResult bindingResult)throws BindException {
//        if (!bindingResult.hasErrors()) {
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.approve, customMessageSource.get(moduleName)),
//                            transferService.updateTransferDecision(transferDecisionPojo)));
//        }else {
//            throw new BindException(bindingResult);
//        }
//
//    }

    @GetMapping("/saruwa-ministry")
    @Operation(summary = "get the saruwa ministry ", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> getSaruwaMinistry() {
        OfficePojo document =  transferService.getSaruwaMinistry();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,customMessageSource.get("DefaultOffice")), document));
    }

    @GetMapping("/generate-saruwa-letter/{id}")
    @Operation(summary = "generate saruwa letter", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> generateSaruwa(@PathVariable Long id) {
        Long document =  saruwaService.generateSaruwaLetter(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("generate"), document));
    }

    @GetMapping("/generate-rawana-letter/{id}")
    @Operation(summary = "generate saruwa letter", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> generateRawana(@PathVariable Long id) {
        Long document =  saruwaService.generateRawana(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("generate"), document));
    }



//    @PreAuthorize("hasPermission(#this.this.permissionName2,'create')")
    @PostMapping("/config")
    @Operation(summary = "add transfer config", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> addTransferConfig(@Valid @RequestBody List<TransferConfigPojo> transferPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get("DefaultOffice")),
                            transferService.addTransferConfig(transferPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/config")
    @Operation(summary = "get transfer config", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> getTransferConfig() {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get("DefaultOffice")),
                            transferService.getTransferConfig()));
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/rawana")
    @Operation(summary = "request a rawana", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> requestRawana(@Valid @RequestBody RawanaDetailsPojo rawanaDetailsPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get("rawana")),
                            transferService.requestRawana(rawanaDetailsPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping("/approve-rawana")
    @Operation(summary = "approve rawana", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> approveRawana(@RequestParam Long id, @RequestParam boolean approved)  {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.approve, customMessageSource.get("rawana")),
                            transferService.approveRawana(id,approved)));
    }

//    @PreAuthorize("hasPermission(#this.this.permissionApproval,'create')")
    @PutMapping("/add-to-tippadi")
    @Operation(summary = "add to tippadi", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> addToTippadi(Set<Long>  transferIds)  {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.add,"Transfer"),
                        transferService.addToTippadi(transferIds)));
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/rawana-approval-by-id/{id}")
    @Operation(summary = "get to be approved rawana", tags = {"TRANSFER MANAGEMENT"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferPojo.class))})
    public ResponseEntity<?> getApproveRawana(@PathVariable Long id)  {
         List<RawanaDetailsResponsePojo> rawanaDetailsResponsePojoList = transferService.getApproveRawana(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get("rawana")),
                        rawanaDetailsResponsePojoList  ));
    }





}
