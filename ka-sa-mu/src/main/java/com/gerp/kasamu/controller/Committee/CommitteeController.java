package com.gerp.kasamu.controller.Committee;

import com.gerp.kasamu.constant.CurdMessages;
import com.gerp.kasamu.constant.PermissionConstants;
import com.gerp.kasamu.pojo.request.CommitteeIndicatorRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuCommitteePojoRequest;
import com.gerp.kasamu.pojo.request.KasamuEvaluatorRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuMasterRequestPojo;
import com.gerp.kasamu.pojo.response.CommitteeIndicatorResponsePojo;
import com.gerp.kasamu.pojo.response.KasamuMasterResponsePojo;
import com.gerp.kasamu.service.CommitteeService;
import com.gerp.kasamu.service.KasamuService;
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
import javax.ws.rs.Path;
import java.util.List;

@Controller
@RequestMapping("/committee")
public class CommitteeController extends BaseController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
        this.moduleName = PermissionConstants.COMMITTEE;
    }


    @PostMapping("/indicator")
    @Operation(summary = "Add a committeeIndicator", tags = {"COMMITTEE"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeIndicatorResponsePojo.class))})
    public ResponseEntity<?> addCommitteeIndicator(@Valid @RequestBody CommitteeIndicatorRequestPojo committeeIndicatorRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = committeeService.addCommitteeIndicator(committeeIndicatorRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.create,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/indicator")
    @Operation(summary = "Update a committeeIndicator", tags = {"COMMITTEE"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return just a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeIndicatorResponsePojo.class))})
    public ResponseEntity<?> updateCommitteeIndicator(@Valid @RequestBody CommitteeIndicatorRequestPojo committeeIndicatorRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = committeeService.updateCommitteeIndicator(committeeIndicatorRequestPojo);
            return  ResponseEntity.ok(
                    successResponse(customMessageSource.get(CurdMessages.update,
                            customMessageSource.get(moduleName.toLowerCase())),
                            id));
        }else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/indicator")
    @Operation(summary = "Get a committeeIndicator list or get by id", tags = {"COMMITTEE"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return list of object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommitteeIndicatorResponsePojo.class))})
    public ResponseEntity<?> getCommitteeIndicator(@RequestParam Long kasamuMasterId, @RequestParam(required = false) Long committeeIndicatorId) {
        List<CommitteeIndicatorResponsePojo> committeeIndicators = committeeService.getCommitteeIndicator(kasamuMasterId,committeeIndicatorId);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CurdMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        committeeIndicators));

    }

    @DeleteMapping("/indicator/{id}")
   public ResponseEntity<?> deleteCommitteeIndicator(@PathVariable Long id) {
         committeeService.deleteCommitteeIndicator(id);
        return  ResponseEntity.ok(
                successResponse(customMessageSource.get(CurdMessages.get,
                        customMessageSource.get(moduleName.toLowerCase())),
                        null));

    }
}
