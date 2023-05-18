package com.gerp.dartachalani.controller.initial;

import com.gerp.dartachalani.dto.ExternalRequestPojo;
import com.gerp.dartachalani.dto.UserDetailsPojo;
import com.gerp.dartachalani.model.external.ExternalRecords;
import com.gerp.dartachalani.service.ExternalRecordsService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/external")
public class ExternalRecordsController extends BaseController {

    private final ExternalRecordsService externalRecordsService;
    private final CustomMessageSource customMessageSource;

    public ExternalRecordsController(
            ExternalRecordsService externalRecordsService,
            CustomMessageSource customMessageSource
    ) {
        this.externalRecordsService = externalRecordsService;
        this.customMessageSource = customMessageSource;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ExternalRequestPojo data, BindingResult bindingResult) throws BindException {
        if(!bindingResult.hasErrors()) {
            ExternalRecords records = externalRecordsService.save(data);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", "Records"),
                            records.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping
    public ResponseEntity<?> getByTaskId(UserDetailsPojo data) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", "Records"),
                        externalRecordsService.getById(data))
        );
    }
}
