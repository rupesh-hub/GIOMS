package com.gerp.kasamu.controller.kasamu;

import com.gerp.kasamu.constant.CurdMessages;
import com.gerp.kasamu.constant.PermissionConstants;
import com.gerp.kasamu.pojo.request.KasamuDetailRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuDetailResponsePojo;
import com.gerp.kasamu.pojo.response.TaskResponse;
import com.gerp.kasamu.service.KasamuDetailService;
import com.gerp.shared.generic.controllers.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/kasamu-detail")
public class KasamuDetailController extends BaseController {

    private final KasamuDetailService kasamuDetailService;

    public KasamuDetailController(KasamuDetailService kasamuDetailService) {
        this.kasamuDetailService = kasamuDetailService;
        this.moduleName = PermissionConstants.KASAMU_TASK;
    }

    @PostMapping
    @Operation(summary = "Add kasamu task details ", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuDetailResponsePojo.class))})
    public ResponseEntity<?> addKasamuMaster(@Valid @RequestBody KasamuDetailRequestPojo kasamuDetailRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuDetailService.addKasamuDetail(kasamuDetailRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping
    @Operation(summary = "Update kasamu task details ", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuDetailResponsePojo.class))})
    public ResponseEntity<?> updateKasamuDetails(@Valid @RequestBody KasamuDetailRequestPojo kasamuDetailRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = kasamuDetailService.updateKasamuDetails(kasamuDetailRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    @Operation(summary = "Get kasamu task details ", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of objects", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuDetailResponsePojo.class))})
    public ResponseEntity<?> getKasamuDetails(@RequestParam(required = false) Long id, @RequestParam String fiscalYear,@RequestParam String evaluationPeriod)  {
           TaskResponse kasamuDetailResponsePojoList = kasamuDetailService.getKasamuDetails(id,fiscalYear,evaluationPeriod);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.get,
                            customMessageSource.get(moduleName.toLowerCase())),
                            kasamuDetailResponsePojoList));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "remove kasamu task details ", tags = {"KASAMU"}, security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> removeKasamuDetails(@PathVariable Long id)  {
       kasamuDetailService.removeKasamuDetails(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CurdMessages.delete,
                        customMessageSource.get(moduleName.toLowerCase())),
                         null));
    }
}
