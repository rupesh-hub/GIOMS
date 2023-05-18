package com.gerp.kasamu.controller.kasamu;

import com.gerp.kasamu.constant.CurdMessages;
import com.gerp.kasamu.constant.PermissionConstants;
import com.gerp.kasamu.pojo.request.KasamuDetailRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuForNoGazettedPojo;
import com.gerp.kasamu.pojo.response.KasamuDetailResponsePojo;
import com.gerp.kasamu.pojo.response.TaskResponse;
import com.gerp.kasamu.service.KasamuDetailService;
import com.gerp.kasamu.service.KasamuNonGazettedService;
import com.gerp.shared.generic.controllers.BaseController;
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

@RestController
@RequestMapping("/kasamu-non-gazetted")
public class KasamuNonGazettedController extends BaseController {

    private final KasamuNonGazettedService kasamuDetailService;

    public KasamuNonGazettedController( KasamuNonGazettedService kasamuDetailService) {
        this.kasamuDetailService = kasamuDetailService;
        this.moduleName = PermissionConstants.KASAMU_TASK;
    }

    @PostMapping
    @Operation(summary = "Add kasamu task details for non gazetted ", tags = {"NonGazetted"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuDetailResponsePojo.class))})
    public ResponseEntity<?> addNonGazetted(@Valid @RequestBody KasamuForNoGazettedPojo KasamuForNoGazettedPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuDetailService.addKasamuNonGazetted(KasamuForNoGazettedPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping
    @Operation(summary = "Update kasamu task details ", tags = {"NonGazetted"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuDetailResponsePojo.class))})
    public ResponseEntity<?> updateNonGazetted(@Valid @RequestBody KasamuForNoGazettedPojo KasamuForNoGazettedPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuDetailService.updateKasamuNonGazetted(KasamuForNoGazettedPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "Get kasamu task details ", tags = {"NonGazetted"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuDetailResponsePojo.class))})
    public ResponseEntity<?> getNonGazetted(@RequestParam(required = false) Long id, @RequestParam String fiscalYear,@RequestParam String evaluationPeriod)  {
           TaskResponse kasamuDetailResponsePojoList = kasamuDetailService.getKasamuNonGazetted(id,fiscalYear,evaluationPeriod);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.get,
                            customMessageSource.get(moduleName.toLowerCase())),
                            kasamuDetailResponsePojoList));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "remove kasamu task details ", tags = {"NonGazetted"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> removeNonGazetted(@PathVariable Long id)  {

        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CurdMessages.delete,
                        customMessageSource.get(moduleName.toLowerCase())),
                        kasamuDetailService.removeKasamuNonGazetted(id)));
    }
}
