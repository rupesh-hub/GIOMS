package com.gerp.dartachalani.controller.kasamu;


import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.StatusPojo;
import com.gerp.dartachalani.dto.kasamu.KasamuRequestPojo;
import com.gerp.dartachalani.dto.kasamu.KasamuStateRequestPojo;
import com.gerp.dartachalani.service.kasamu.KasamuService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/darta-kasamu")
public class KasamuController extends BaseController {

    private final KasamuService kasamuService;
    private final CustomMessageSource customMessageSource;

    public KasamuController(KasamuService kasamuService, CustomMessageSource customMessageSource) {
        this.kasamuService = kasamuService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.KASAMU_MODULE_NAME;
    }

    /**
     * this api is used to get save kasamu
     **/
    @PostMapping
    public ResponseEntity<?> save(@Valid @ModelAttribute KasamuRequestPojo kasamuRequestPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())), kasamuService.save(kasamuRequestPojo)));
    }

    /**
     * this api is used to get kasamu by id
     **/
    @GetMapping
    public ResponseEntity<?> view(@RequestParam Long id) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), kasamuService.getById(id)));
    }

    // not used for now
    @PostMapping("/forward")
    public ResponseEntity<?> forward(@RequestBody KasamuStateRequestPojo kasamuStateRequestPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())), kasamuService.forward(kasamuStateRequestPojo)));
    }

    /**
     * this api is used to get list of kasamu created by login user
     **/
    @PostMapping("/created/list")
    public ResponseEntity<?> createdList(@RequestBody GetRowsRequest getRowsRequest) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), kasamuService.getCreatedKasamuList(getRowsRequest)));
    }

    /**
     * this api is used to get list of kasamu that are forwared to login user
     **/
    @PostMapping("/inbox/list")
    public ResponseEntity<?> inboxList(@RequestBody GetRowsRequest getRowsRequest) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), kasamuService.getInboxKasamuList(getRowsRequest)));
    }

    /**
     * this api is used to get list of finalized kasamu
     **/
    @PostMapping("/finalized/list")
    public ResponseEntity<?> finalized(@RequestBody GetRowsRequest getRowsRequest) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), kasamuService.getFinalizedKasamuList(getRowsRequest)));
    }

    @PostMapping("/own/list")
    public ResponseEntity<?> ownList(@RequestBody GetRowsRequest getRowsRequest) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())), kasamuService.getEmployeeList(getRowsRequest)));
    }

    /**
     * this api is used to finalize kasamu
     **/
    @PutMapping("/finalize")
    public ResponseEntity<?> finalizeKasamu(@RequestBody StatusPojo statusPojo) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())), kasamuService.finalizedKasamu(statusPojo)));
    }

    @PostMapping("/search-recommendation")
    public ResponseEntity<?> createdSearchRecommendation(@RequestBody GetRowsRequest getRowsRequest) {
        return ResponseEntity.ok(successResponse("Kasamu created search-recommendation fetched successfully", kasamuService.getSearchRecommendationCreate(getRowsRequest)));
    }

    @PostMapping("/inbox/search-recommendation")
    public ResponseEntity<?> inboxSearchRecommendation(@RequestBody GetRowsRequest getRowsRequest) {
        return ResponseEntity.ok(successResponse("Kasamu inbox search-recommendation fetched successfully", kasamuService.getSearchRecommendationInbox(getRowsRequest)));
    }

    @PostMapping("/finalized/search-recommendation")
    public ResponseEntity<?> finalizedSearchRecommendation(@RequestBody GetRowsRequest getRowsRequest) {
        return ResponseEntity.ok(successResponse("Kasamu inbox search-recommendation fetched successfully", kasamuService.getSearchRecommendationFinalized(getRowsRequest)));
    }
}
