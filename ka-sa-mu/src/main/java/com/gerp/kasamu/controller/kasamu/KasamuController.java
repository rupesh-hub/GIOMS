package com.gerp.kasamu.controller.kasamu;

import com.gerp.kasamu.constant.PermissionConstants;
import com.gerp.kasamu.pojo.request.KasamuCommitteePojoRequest;
import com.gerp.kasamu.pojo.request.KasamuEvaluatorHalfYearlyRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuMasterEvaluatorRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuMasterRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuMasterResponsePojo;
import com.gerp.kasamu.service.KasamuService;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
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
@RequestMapping("/kasamu")
public class KasamuController extends BaseController {

    private final KasamuService kasamuService;

    public KasamuController(KasamuService kasamuService) {
        this.kasamuService = kasamuService;
        this.moduleName = PermissionConstants.KASAMU;
        this.moduleName2 = PermissionConstants.REVIEW;
    }


    @PostMapping
    @Operation(summary = "Create a new kasamu request", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> addKasamuMaster(@Valid @RequestBody KasamuMasterRequestPojo kasamuMasterRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuService.addKasamuMaster(kasamuMasterRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

//    @PutMapping
//    @Operation(summary = "Update a new kasamu", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
//    public ResponseEntity<?> updateKasamuMaster(@Valid @RequestBody KasamuMasterRequestPojo kasamuMasterRequestPojo, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            Long id = kasamuService.updateKasmuMaster(kasamuMasterRequestPojo);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.update,
//                            customMessageSource.get(moduleName.toLowerCase())),
//                            id));
//        }else {
//            throw new BindException(bindingResult);
//        }
//    }


//    @GetMapping()
//    @Operation(summary = "Get all kasamu", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
//    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
//    public ResponseEntity<?> getAllKasamu(@RequestParam(required = false) String employeePisCode)  {
//            List<KasamuMasterResponsePojo> kasamuMasterResponsePojoList = kasamuService.getAllKasamu(employeePisCode);
//            return  ResponseEntity.ok(
//                    successResponse(customMessageSource.get(CrudMessages.get,
//                            customMessageSource.get(moduleName.toLowerCase())),
//                            kasamuMasterResponsePojoList));
//    }

    @GetMapping("/to-review-supervisor")
    @Operation(summary = "Get all kasamu to be reviewed by supervisor", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> getAllKasamuToBeReviewBySupervisor()  {
        List<KasamuMasterResponsePojo> kasamuMasterResponsePojoList = kasamuService.getAllKasamuToBeReviewBySupervisor();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuMasterResponsePojoList));
    }

    @GetMapping("/to-review-evaluator")
    @Operation(summary = "Get all kasamu to be reviewed by purnarawalkarta", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> getAllKasamuToBeReviewByPurnarawal()  {
        List<KasamuMasterResponsePojo> kasamuMasterResponsePojoList = kasamuService.getAllKasamuToBeReviewByPurnarawal();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuMasterResponsePojoList));
    }

    @GetMapping("/to-review-committee")
    @Operation(summary = "Get all kasamu to be reviewed by committee", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return List of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> getAllKasamuToBeReviewByCommittee()  {
        List<KasamuMasterResponsePojo> kasamuMasterResponsePojoList = kasamuService.getAllKasamuToBeReviewByCommittee();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuMasterResponsePojoList));
    }

    @GetMapping("/get-by-id")
    @Operation(summary = "Get all kasamu", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> getKasamuMasterById(@RequestParam(required = false) Long id,@RequestParam(required = false) String fiscalYear, @RequestParam(required = false)String pisCode)  {
        KasamuMasterResponsePojo kasamuMasterResponsePojo = kasamuService.getKasamuMasterById(id,fiscalYear,pisCode);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuMasterResponsePojo));
    }

    @GetMapping("/pisCode-fiscal-year")
    @Operation(summary = "Get all kasamu", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> getKasamuMasterByEmployee(@RequestParam String fiscalYear, @RequestParam String pisCode)  {
        List<KasamuMasterResponsePojo> kasamuMasterResponsePojo = kasamuService.getKasamuMasterByEmployeePisCodeAndFiscalYear(fiscalYear,pisCode);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuMasterResponsePojo));
    }

    @GetMapping("get-all-drafts/{employeeCode}")
    @Operation(summary = "Get all the saved draft for the employee", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> getDraftsForEmployee(@PathVariable String employeeCode)  {
        List<KasamuMasterResponsePojo> kasamuMasterResponsePojo = kasamuService.getDrafts(employeeCode);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuMasterResponsePojo));
    }

    @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteKasamuMasterById(@PathVariable Long id)  {
         kasamuService.deleteKasamuMaster(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.delete,
                        customMessageSource.get(moduleName.toLowerCase())),
                        null));
    }

    @PutMapping("/submit-review")
    @Operation(summary = "Submit the review for the kasamu by supervisor and evaluator", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> submitReview(@Valid @RequestBody KasamuMasterEvaluatorRequestPojo kasamuEvaluatorRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuService.submitReview(kasamuEvaluatorRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit,
                            customMessageSource.get(moduleName2.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/submit-review-half-yearly")
    @Operation(summary = "Submit the review for the half yearly kasamu by supervisor ", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> submitReviewHalfYearly(@Valid @RequestBody KasamuEvaluatorHalfYearlyRequestPojo kasamuEvaluatorRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuService.submitReviewHalfYearly(kasamuEvaluatorRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit,
                            customMessageSource.get(moduleName2.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/submit-review-by-employee")
    @Operation(summary = "Submit the review for the half yearly kasamu by employee ", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> submitReviewEmployee(@Valid @RequestBody KasamuEvaluatorHalfYearlyRequestPojo kasamuEvaluatorRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuService.submitReviewByEmplpoyee(kasamuEvaluatorRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit,
                            customMessageSource.get(moduleName2.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }


    @PutMapping("/review-committee")
    @Operation(summary = "Submit the review for the kasamu by committee", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> submitReviewByCommittee(@Valid @RequestBody KasamuCommitteePojoRequest committeeIndicatorRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuService.submitReviewByCommittee(committeeIndicatorRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit,
                            customMessageSource.get(moduleName2.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/decision-committee-member")
    @Operation(summary = "Submit the review for the kasamu by committee", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> decisionByCommitteeMembers(@RequestParam Long id, @RequestParam Boolean decision){
            Long ids = kasamuService.decisionByCommitteeMembers(id,decision);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit,
                            customMessageSource.get(moduleName2.toLowerCase())),ids));
    }


}
