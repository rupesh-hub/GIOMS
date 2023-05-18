package com.gerp.usermgmt.controller.transferMgmt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.transfer.*;
import com.gerp.usermgmt.services.transfer.TransferConfigService;
import com.gerp.usermgmt.services.transfer.TransferService;
import com.gerp.usermgmt.services.transfer.impl.SaruwaService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/transfer-authority")
public class TransferConfigController extends BaseController {

    private final TransferConfigService transferConfigService;

    public TransferConfigController( TransferConfigService transferConfigService) {
        this.transferConfigService = transferConfigService;
        this.moduleName= PermissionConstants.TRANSFER_AUTHORITY;
        this.permissionName= PermissionConstants.TRANSFEREMPLOYEE+"_"+PermissionConstants.TRANSFERAUTHORITY;
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    @Operation(summary = "add transfer authority", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferAuthorityRequestPojo.class))})
    public ResponseEntity<?> addTransferAuthority(@Valid @RequestBody TransferAuthorityRequestPojo transferAuthorityRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create, customMessageSource.get(moduleName)),
                            transferConfigService.addTransferAuthority(transferAuthorityRequestPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/employee")
    @Operation(summary = "get employee to be transfered  by office ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferAuthorityResponsePojo.class))})
    public ResponseEntity<?> getEmployeeToBeTransferTransferAuthority(@RequestParam(required = false) String employeeName,
                                                                      @RequestParam(required = false,defaultValue = "100") int limit,
                                                                      @RequestParam(required = false,defaultValue = "1") int page,
                                                                      @RequestParam Boolean isWithSelected,
                                                                      @RequestParam String officeCode) {
        Page<DetailPojo> employee =  transferConfigService.getEmployeeToBeTransfered(employeeName,limit,page,isWithSelected,officeCode);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),employee ));
    }

    @GetMapping("/transfer-offices")
    @Operation(summary = "get offices where employee will be transfer ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferAuthorityResponsePojo.class))})
    public ResponseEntity<?> getTransferOffices(@RequestParam(required = false) String officeName,
                                                                      @RequestParam(required = false,defaultValue = "1200") int limit,
                                                                      @RequestParam(required = false,defaultValue = "1") int page,
                                                                        @RequestParam String officeCode,
                                                                        @RequestParam String districtCode) {
        Page<OfficePojo> employee =  transferConfigService.getTransferOffices(officeName,limit,page,officeCode,districtCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),employee ));
    }

    //    @PreAuthorize("hasPermission(#this.PaginationInterceptorthis.permissionName,'create')")
    @GetMapping
    @Operation(summary = "get transfer authority list ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferAuthorityResponsePojo.class))})
    public ResponseEntity<?> getTransferAuthority() {
        List<TransferAuthorityResponsePojo>  transfers =  transferConfigService.getTransferConfig();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transfers ));
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @GetMapping("/offices")
    @Operation(summary = "get transfer request office list ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DetailPojo.class))})
    public ResponseEntity<?> getTransferAuthorityOffice() {
        List<DetailPojo> transfers =  transferConfigService.getTransferAuthorityOffice();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transfers ));
    }

    @GetMapping("/transfer-from-offices")
    @Operation(summary = "get transfer from offices list ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DetailPojo.class))})
    public ResponseEntity<?> getTransferFromOffice(@RequestParam(required = false) String officeName,
                                                   @RequestParam(required = false,defaultValue = "1200") int limit,
                                                   @RequestParam(required = false,defaultValue = "1") int page,
                                                   @RequestParam String districtCode) {
        Page<OfficePojo> transfers =  transferConfigService.getTransferFromOffice(officeName,limit,page,districtCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transfers ));
    }
    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PutMapping
    @Operation(summary = "update transfer authority", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransferAuthorityRequestPojo.class))})
    public ResponseEntity<?> updateTransferAuthority(@Valid @RequestBody TransferAuthorityRequestPojo transferAuthorityRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.update, customMessageSource.get(moduleName)),
                            transferConfigService.updateTransferAuthority(transferAuthorityRequestPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @DeleteMapping("/{id}")
    @Operation(summary = "delete transfer authority by id ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> getTransferAuthorityOffice(@PathVariable Integer id) {
       Integer  transfers =  transferConfigService.deleteAuthortiyById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete, customMessageSource.get(moduleName)),transfers ));
    }

    @GetMapping("/date-range")
    @Operation(summary = "get date-range ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DetailPojo.class))})
    public ResponseEntity<GlobalApiResponse> getDateRange(@RequestParam boolean currentDate,
                                                          @RequestParam int currentFiscalYear) {

        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transferConfigService.getDateRange(currentDate,currentFiscalYear) ));
    }

    @GetMapping("/year-date-range")
    @Operation(summary = "get year date range ", tags = {"TRANSFER CONFIG"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DetailPojo.class))})
    public ResponseEntity<GlobalApiResponse> getYearDateRange( @RequestParam int currentFiscalYear) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),transferConfigService.getYearDateRange(currentFiscalYear) ));
    }

}
