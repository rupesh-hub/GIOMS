package com.gerp.kasamu.controller.kasamu;

import com.gerp.kasamu.pojo.KasamuRequestReviewListPojo;
import com.gerp.kasamu.pojo.request.KasamuMasterRequestPojo;
import com.gerp.kasamu.pojo.request.RequestForViewPojo;
import com.gerp.kasamu.pojo.response.KasamuMasterResponsePojo;
import com.gerp.kasamu.service.KasamuRequestForViewService;
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
@RequestMapping("/view")
public class KasamuRequestForViewController extends BaseController {

    private final KasamuRequestForViewService kasamuRequestForViewService;

    public KasamuRequestForViewController(KasamuRequestForViewService kasamuRequestForViewService) {
        this.kasamuRequestForViewService = kasamuRequestForViewService;
        this.moduleName = "request";
    }

    @PostMapping
    @Operation(summary = "Create a new kasamu view request", tags = {"View-request"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> addKasamuMaster(@Valid @RequestBody RequestForViewPojo requestForViewPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuRequestForViewService.requestForView(requestForViewPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CrudMessages.submit,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "get all the kasamu requested for the view", tags = {"View-request"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> getRequestList() {
        List<KasamuRequestReviewListPojo> kasamuRequestReviewListPojos = kasamuRequestForViewService.getRequestedList();
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuRequestReviewListPojos));
    }

    @PutMapping("/decision")
    @Operation(summary = "give the decision to the view", tags = {"View-request"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuMasterResponsePojo.class))})
    public ResponseEntity<?> decision(@RequestParam Long id, @RequestParam Boolean decision) {
        Long respo =  kasamuRequestForViewService.decisionGivenBy(id,decision);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.update,
                        customMessageSource.get(moduleName.toLowerCase())),
                        respo));
    }



}
