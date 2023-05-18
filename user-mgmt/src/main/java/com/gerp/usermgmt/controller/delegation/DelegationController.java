package com.gerp.usermgmt.controller.delegation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.annotations.DelegationLogExecution;
import com.gerp.usermgmt.pojo.delegation.TempDelegationPojo;
import com.gerp.usermgmt.pojo.delegation.TempDelegationResponsePojo;
import com.gerp.usermgmt.services.delegation.DelegationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/delegation")
public class DelegationController extends BaseController {


    private final DelegationService delegationService;

    public DelegationController(DelegationService delegationService) {
        this.delegationService = delegationService;
        this.moduleName = "Delegation";
    }

    @DelegationLogExecution
    @PostMapping
    @Operation(summary = "create a temporary delegation request", tags = {"DELEGATION"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TempDelegationPojo.class))})
    public ResponseEntity<?> addDelegation(@RequestBody TempDelegationPojo tempDelegationPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get(moduleName)),
                            delegationService.addDelegation(tempDelegationPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @DelegationLogExecution
    @PutMapping
    @Operation(summary = "update a temporary delegation request", tags = {"DELEGATION"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TempDelegationPojo.class))})
    public ResponseEntity<?> updateDelegation(@RequestBody TempDelegationPojo tempDelegationPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit, customMessageSource.get(moduleName)),
                            delegationService.updateDelegation(tempDelegationPojo)));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "get a temporary delegation request", tags = {"DELEGATION"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TempDelegationResponsePojo.class))})
    public ResponseEntity<?> getTemporaryDelegation(@RequestParam(required = false) String searchKey,
                                                       @RequestParam(required = false,defaultValue = "10") int limit,
                                                       @RequestParam(required = false) Boolean isReassignment,
                                                       @RequestParam(required = false,defaultValue = "1") int page ) {
        Page<TempDelegationResponsePojo> delegation = delegationService.getTemporaryDelegation(searchKey,limit,page,isReassignment);
        return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),delegation));
    }

    @GetMapping("/list")
    @Operation(summary = "get a temporary delegation details", tags = {"DELEGATION"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TempDelegationResponsePojo.class))})
    public ResponseEntity<?> getTemporaryDelegationList(@RequestParam(required = false) String searchKey,
                                                    @RequestParam(required = false,defaultValue = "10") int limit,
                                                    @RequestParam Boolean isDelegatedSelf,
                                                        @RequestParam(required = false) Boolean isReassignment,
                                                    @RequestParam(required = false,defaultValue = "1") int page ) {
        Page<TempDelegationResponsePojo> delegation = delegationService.getTemporaryDelegationList(searchKey,limit,page,isDelegatedSelf,isReassignment);
        return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),delegation));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete temporary delegation request", tags = {"DELEGATION"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TempDelegationResponsePojo.class))})
    public ResponseEntity<?> deleteTemporaryDelegation(@PathVariable int id) {
        int delegation = delegationService.deletTempDelegation(id);
        return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.delete, customMessageSource.get(moduleName)),delegation));
    }
    @GetMapping("/{id}")
    @Operation(summary = "get a delegation details by id", tags = {"DELEGATION"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TempDelegationResponsePojo.class))})
    public ResponseEntity<?> getTemporaryDelegationById(@PathVariable Integer id) {
      TempDelegationResponsePojo delegation = delegationService.getTemporaryDelegationById(id);
        return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),delegation));
    }

    // for test
    @GetMapping("/server-time")
     public ResponseEntity<?> getTemporaryDelegationById() {
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return ResponseEntity.ok(successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)), formattedDateTime));
    }

    @GetMapping("/get-delegation")
    public ResponseEntity<?> getDelegation(@RequestParam("pisCode") String pisCode, @RequestParam("isReassignment") Boolean isReassignmen){
        List<TempDelegationResponsePojo>  tempDelegationResponsePojos = delegationService.getAllDelegation(pisCode, isReassignmen);
        return ResponseEntity.ok(successResponse( customMessageSource.get(moduleName), tempDelegationResponsePojos));
    }

}
