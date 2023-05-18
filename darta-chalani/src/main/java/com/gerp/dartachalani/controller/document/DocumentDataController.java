package com.gerp.dartachalani.controller.document;

import com.gerp.dartachalani.service.DocumentDataService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
public class DocumentDataController extends BaseController {

    private final CustomMessageSource customMessageSource;
    private final DocumentDataService documentDataService;

    public DocumentDataController(CustomMessageSource customMessageSource,
                                  DocumentDataService documentDataService) {
        this.customMessageSource = customMessageSource;
        this.documentDataService = documentDataService;
    }


    @GetMapping("/data")
    public ResponseEntity<?> getDocData(@RequestParam("id") Long id) {
        return ResponseEntity.ok(
               successResponse( customMessageSource.get("crud.get", customMessageSource.get("document")),
                       documentDataService.getDocumentData(id))
        );
    }

}
