package com.gerp.dartachalani.controller.DigitalSignature;

import com.gerp.dartachalani.dto.DigitalSignatureDto;
import com.gerp.dartachalani.dto.DispatchedResponsePojo;
import com.gerp.dartachalani.service.digitalSignature.GenerateHashService;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class DigitalSignatureController extends BaseController {

    private GenerateHashService generateHashService;

    public DigitalSignatureController(GenerateHashService generateHashService) {
        this.generateHashService = generateHashService;
    }

    @PostMapping("/generate/hash")
    public ResponseEntity<?> generateHasValue(@Valid @RequestBody DigitalSignatureDto digitalSignatureDto) {
        DigitalSignatureDto hasValue = generateHashService.generateHash(digitalSignatureDto);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("success.verify", customMessageSource.get("hash")),
                        hasValue
                )
        );
    }

    @PostMapping("/signature/verify")
    public ResponseEntity<?> verify(@RequestBody DigitalSignatureDto digitalSignatureDto) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.verify", customMessageSource.get("signature")), generateHashService.verify(digitalSignatureDto)));
    }
}
