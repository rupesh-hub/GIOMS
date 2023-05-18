package com.gerp.sbm.controller;

import com.gerp.sbm.Proxy.EmployeeDetailProxy;
import com.gerp.sbm.constant.PermissionConstants;
import com.gerp.sbm.pojo.RequestPojo.*;
import com.gerp.sbm.service.SampatiBibaranService;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
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
public class SampatiBibaranController extends BaseController {

    @Autowired
    private SampatiBibaranService sampatiBibaranService;
//
//    public SampatiBibaranController(SampatiBibaranController ) {
//        this.valuableItemsService = valuableItemsService;
//        this.moduleName = PermissionConstants.VALUABLE_TEAMS;
//    }

    // fixed asset
    @PostMapping("/fixed_asset")
    @Operation(summary = "Add a fixedAsset related details", tags = {"FIXEDASSET"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addFixedAsset(@Valid @RequestBody FixedAssetRequestPojo fixedAssetRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addFixedAsset(fixedAssetRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "FIXED_ASSET"),null));

        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/fixed_asset")
    @Operation(summary = "Add a fixedAsset related details", tags = {"FIXEDASSET"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editFixedAsset(@Valid @RequestBody FixedAssetRequestPojo fixedAssetRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editFixedAsset(fixedAssetRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "FIXED_ASSET"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/fixed_asset")
    @Operation(summary = "Add a fixedAsset related details", tags = {"FIXEDASSET"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> listFixedAsset(@RequestParam String piscode) {
        if (piscode!=null) {
            //return new ResponseEntity<>(sampatiBibaranService.listAsset(piscode),HttpStatus.OK);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "FIXED_ASSET"),sampatiBibaranService.listAsset(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }


    @DeleteMapping("/fixed_asset")
    @Operation(summary = "Delete a fixedAsset related details", tags = {"FIXEDASSET"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteFixedAsset(@RequestParam Long id)  {
        if (id!=null) {
            sampatiBibaranService.deleteFixedAsset(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.delete,
                            "FIXED_ASSET"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }

    // bank details

    @PostMapping("/bank_details")
    @Operation(summary = "Add a BANK_DETAILS related details", tags = {"BANK_DETAILS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addBankDetails(@Valid @RequestBody BankDetailsRequestPojo bankDetailsRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addBankDetails(bankDetailsRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "BANK_DETAILS"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/bank_details")
    @Operation(summary = "Edit a BANK_DETAILS related details", tags = {"BANK_DETAILS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editBankDetails(@Valid @RequestBody BankDetailsRequestPojo bankDetailsRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editBankDetails(bankDetailsRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "BANK_DETAILS"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/bank_details")
    @Operation(summary = "List a BANK_DETAILS related details", tags = {"BANK_DETAILS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> listBankDetails(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "BANK_DETAILS"),sampatiBibaranService.listBankDetails(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }

    @DeleteMapping("/bank_details")
    @Operation(summary = "Delete a BANK_DETAILS related details", tags = {"BANK_DETAILS"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteBankDetails(@RequestParam Long id){
        if (id!=null) {
            sampatiBibaranService.deleteBankDetails(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.delete,
                            "BANK_DETAILS"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }


    //share details


    @PostMapping("/share_details")
    @Operation(summary = "Add a Share related details", tags = {"SHARE"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addShare(@Valid @RequestBody ShareRequestPojo shareRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addShare(shareRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "SHARE"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/share_details")
    @Operation(summary = "Edit a Share related details", tags = {"SHARE"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editShare(@Valid @RequestBody ShareRequestPojo shareRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editShare(shareRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "SHARE"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/share_details")
    @Operation(summary = "List a Share related details", tags = {"SHARE"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> listShare(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {

            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "SHARE"),sampatiBibaranService.listShare(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }


    @DeleteMapping("/share_details")
    @Operation(summary = "Detete a Share related details", tags = {"SHARE"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "delete status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteShare(@RequestParam Long id) throws BindException {
        if (id!=null) {
            sampatiBibaranService.deleteShare(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.delete,
                            "SHARE"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }

    //loan

    @PostMapping("/loan")
    @Operation(summary = "Add a Loan related details", tags = {"LOAN"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addLoan(@Valid @RequestBody LoanRequestPojo loanRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addLoan(loanRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "LOAN"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/loan")
    @Operation(summary = "Edit a Loan related details", tags = {"LOAN"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editLoan(@Valid @RequestBody LoanRequestPojo loanRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editLoan(loanRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "LOAN"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/loan")
    @Operation(summary = "List a Loan related details", tags = {"LOAN"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editLoan(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "LOAN"),sampatiBibaranService.listLoan(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }


    @DeleteMapping("/loan")
    @Operation(summary = "Delete a Loan related details", tags = {"LOAN"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteLoan(@RequestParam Long id) {
        if (id!=null) {
            sampatiBibaranService.deleteLoan(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.delete,
                            "LOAN"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }


    //other details
    @PostMapping("/other_details")
    @Operation(summary = "Add a OtherDetails related details", tags = {"OtherDetails"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addOtherDetails(@Valid @RequestBody OtherDetailRequestPojo otherDetailRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addOtherDetails(otherDetailRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "OtherDetails"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/other_details")
    @Operation(summary = "Edit a OtherDetails related details", tags = {"OtherDetails"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editOtherDetails(@Valid @RequestBody OtherDetailRequestPojo otherDetailRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editOtherDetails(otherDetailRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "OtherDetails"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/other_details")
    @Operation(summary = "List a OtherDetails related details", tags = {"OtherDetails"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> listOtherDetails(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "OtherDetails"),sampatiBibaranService.listOtherDetails(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }


    @DeleteMapping("/other_details")
    @Operation(summary = "Edit a OtherDetails related details", tags = {"OtherDetails"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteOtherDetails(@RequestParam Long id) throws BindException {
        if (id!=null) {
            sampatiBibaranService.deleteOtherDetails(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.delete,
                            "OtherDetails"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }


    //valuable items

    @PostMapping("/valueable_item")
    @Operation(summary = "Add a ValuableItems related details", tags = {"ValuableItems"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addValuableItems(@Valid @RequestBody ValuableItemsRequestPojo valuableItemsRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addValuableItems(valuableItemsRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "ValuableItems"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/valueable_item")
    @Operation(summary = "Edit a ValuableItems related details", tags = {"ValuableItems"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editValuableItems(@Valid @RequestBody ValuableItemsRequestPojo valuableItemsRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editValuableItems(valuableItemsRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "ValuableItems"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/valueable_item")
    @Operation(summary = "List a ValuableItems related details", tags = {"ValuableItems"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> listValuableItems(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "ValuableItems"),sampatiBibaranService.listValuableItems(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }


    @DeleteMapping("/valueable_item")
    @Operation(summary = "Delete a ValuableItems related details", tags = {"ValuableItems"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteValuableItems(@RequestParam Long id)  {
        if (id!=null) {
            sampatiBibaranService.deleteValuableItems(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "ValuableItems"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }


    //AgricultureDetail

    @PostMapping("/agriculture")
    @Operation(summary = "Add a AgricultureDetail related details", tags = {"AgricultureDetail"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addAgricultureDetail(@Valid @RequestBody AgricultureDetailRequestPojo agricultureDetailRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addAgricultureDetail(agricultureDetailRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "AgricultureDetail"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/agriculture")
    @Operation(summary = "Edit a AgricultureDetail related details", tags = {"AgricultureDetail"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editAgricultureDetail(@Valid @RequestBody AgricultureDetailRequestPojo agricultureDetailRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editAgricultureDetail(agricultureDetailRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "AgricultureDetail"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/agriculture")
    @Operation(summary = "List a AgricultureDetail related details", tags = {"AgricultureDetail"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> listAgricultureDetail(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.get,
                            "AgricultureDetail"),sampatiBibaranService.listAgricultureDetail(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }


    @DeleteMapping("/agriculture")
    @Operation(summary = "Delete a AgricultureDetail related details", tags = {"AgricultureDetail"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteAgricultureDetail(@RequestParam Long id)  {
        if (id!=null) {
            sampatiBibaranService.deleteAgricultureDetail(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.delete,
                            "AgricultureDetail"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }


    //Vehicle

    @PostMapping("/vehicle")
    @Operation(summary = "Add a Vehicle related details", tags = {"Vehicle"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> addVehicle(@Valid @RequestBody VehicleRequestPojo vehicleRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.addVehicle(vehicleRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "Vehicle"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/vehicle")
    @Operation(summary = "Edit a Vehicle related details", tags = {"Vehicle"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> editVehicle(@Valid @RequestBody VehicleRequestPojo vehicleRequestPojo, BindingResult bindingResult,@RequestParam Long id) throws BindException {
        if (!bindingResult.hasErrors()) {
            sampatiBibaranService.editVehicle(vehicleRequestPojo,id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "Vehicle"),null));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/vehicle")
    @Operation(summary = "List a Vehicle related details", tags = {"Vehicle"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> listVehicle(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "Vehicle"),sampatiBibaranService.listVehicle(piscode)));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }


    @DeleteMapping("/vehicle")
    @Operation(summary = "Delete a Vehicle related details", tags = {"Vehicle"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> deleteVehicle(@RequestParam Long id) {
        if (id!=null) {
            sampatiBibaranService.deleteVehicle(id);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.delete,
                            "Vehicle"),null));
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id can not be empty");
        }
    }


//    @PostMapping()
//    @Operation(summary = "List a Sampati Master details", tags = {"SampatiMaster"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
//    public ResponseEntity<?> saveSampati(@RequestParam String piscode,@RequestParam String fiscal_year) throws BindException {
//        if (piscode!=null) {
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.create,
//                            "SampatiMaster"),sampatiBibaranService.saveSampati(piscode,fiscal_year)));
//        }else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
//        }
//    }

//    @PostMapping()
//    @Operation(summary = "Add a Vehicle related details", tags = {"Vehicle"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
//    public ResponseEntity<?> addVehicle(@Valid @RequestBody VehicleRequestPojo vehicleRequestPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            sampatiBibaranService.addVehicle(vehicleRequestPojo);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.create,
//                            "Vehicle"),null));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }

//    @GetMapping("/user")
//    public void getemployee(){
//        employeeDetailProxy.getEmployeeDetailMinimal("201545");
//    }

    @PostMapping()
    @Operation(summary = "Post Sampati Master details", tags = {"SampatiMaster"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "post status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> saveSampati(@RequestParam String piscode) throws BindException {
        if (piscode!=null) {
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            "SampatiMaster"),sampatiBibaranService.saveSampati(piscode)));

        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"PisCode can not be empty");
        }
    }

    @GetMapping("/user")
    @Operation(summary = "List a User Master details", tags = {"User"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "get status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FixedAssetRequestPojo.class))})
    public ResponseEntity<?> user() {
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        "User"),sampatiBibaranService.user()));
    }


}
