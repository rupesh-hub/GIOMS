package com.gerp.dartachalani.controller.LetterTransfer;

import com.gerp.dartachalani.dto.LetterTransferPojo;
import com.gerp.dartachalani.service.LetterTransferService;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.sun.mail.iap.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/letter-transfer")
public class LetterTransferController extends BaseController {

    private final LetterTransferService letterTransferService;

    public LetterTransferController(LetterTransferService letterTransferService) {
        this.letterTransferService = letterTransferService;
        this.moduleName = "LetterTransfer";
    }

    @GetMapping("/users")
    public ResponseEntity<GlobalApiResponse> getAllUserBySection(@RequestParam(name = "id") Long sectionCode) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", "Users"),
                letterTransferService.findUserBySection(sectionCode)));
    }

    @PostMapping("/persist")
    public ResponseEntity<GlobalApiResponse> persistLetterTransfer(@RequestBody LetterTransferPojo data) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.status", "Letters"),
                letterTransferService.persistLetterTransfer(data)));
    }

    @GetMapping("/history")
    public ResponseEntity<GlobalApiResponse> getLetterHistoryByOfficeCode() {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", "History"),
                letterTransferService.getLetterTransferHistoryByOfficeCode()));
    }

}
