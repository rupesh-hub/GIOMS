//package com.gerp.sbm.controller;
//
//import com.gerp.sbm.constant.PermissionConstants;
//import com.gerp.sbm.pojo.RequestPojo.CashAndGoldRequestPojo;
//import com.gerp.sbm.pojo.ResponsePojo.CashAndGoldResponsePojo;
//import com.gerp.sbm.service.ValuableItemsService;
//import com.gerp.shared.generic.controllers.BaseController;
//import com.gerp.shared.utils.CrudMessages;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindException;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.List;
//
//@RestController
//@RequestMapping("/valuable")
//public class ValuableItemsController extends BaseController {
//
//    private final ValuableItemsService valuableItemsService;
//
//    public ValuableItemsController(ValuableItemsService valuableItemsService) {
//        this.valuableItemsService = valuableItemsService;
//        this.moduleName = PermissionConstants.VALUABLE_TEAMS;
//    }
//
//    @PostMapping()
//    @Operation(summary = "Add a valuable items", tags = {"VALUABLE"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CashAndGoldResponsePojo.class))})
//    public ResponseEntity<?> addValuableItems(@Valid @RequestBody CashAndGoldRequestPojo cashAndGoldRequestPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            Long id = valuableItemsService.addValuableItems(cashAndGoldRequestPojo);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.create,
//                            customMessageSource.get(moduleName.toLowerCase())),
//                            id));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @PutMapping()
//    @Operation(summary = "update a valuable items", tags = {"VALUABLE"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CashAndGoldResponsePojo.class))})
//    public ResponseEntity<?> updateValuableItems(@Valid @RequestBody CashAndGoldRequestPojo cashAndGoldRequestPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            Long id = valuableItemsService.updateValuableItems(cashAndGoldRequestPojo);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.update,
//                            customMessageSource.get(moduleName.toLowerCase())),
//                            id));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @GetMapping()
//    @Operation(summary = "get all valuable items or just one by id", tags = {"VALUABLE"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CashAndGoldResponsePojo.class))})
//    public ResponseEntity<?> getValuableItems(@RequestParam(required = false) Long id, @RequestParam(required = false, defaultValue = "desc") String sortOrder){
//            List<CashAndGoldResponsePojo> cashAndGoldResponsePojos= valuableItemsService.getValuableItems(id, sortOrder);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.update,
//                            customMessageSource.get(moduleName.toLowerCase())),
//                            cashAndGoldResponsePojos));
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete valuable items  by id", tags = {"VALUABLE"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CashAndGoldResponsePojo.class))})
//    public ResponseEntity<?> deleteValuableItems(@PathVariable Long id){
//       Long reid = valuableItemsService.deleteValuableItemsById(id);
//        return  ResponseEntity.ok(
//                successResponse(customMessageSource.get(CrudMessages.update,
//                        customMessageSource.get(moduleName.toLowerCase())),
//                        reid));
//    }
//}
