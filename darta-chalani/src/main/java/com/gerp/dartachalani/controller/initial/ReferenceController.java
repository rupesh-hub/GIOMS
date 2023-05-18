package com.gerp.dartachalani.controller.initial;

import com.gerp.dartachalani.dto.UserDetailsPojo;
import com.gerp.dartachalani.service.ReferenceService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reference")
public class ReferenceController extends BaseController {

    private final ReferenceService referenceService;

    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @PostMapping
    public ResponseEntity<GlobalApiResponse> getPaginatedData(@RequestBody GetRowsRequest paginatedRequest) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get("reference.letter")),
                        referenceService.getReferences(paginatedRequest))
        );
    }

    @PostMapping("/involved")
    public ResponseEntity<GlobalApiResponse> getInvolvedUsers(@RequestBody UserDetailsPojo request ) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get("usermgmt")),
                        referenceService.getInvolvedUsers(request))
        );
    }

}
