package com.gerp.dartachalani.controller.document;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.DispatchedResponsePojo;
import com.gerp.dartachalani.dto.MemoResponsePojo;
import com.gerp.dartachalani.dto.ReceivedLetterResponsePojo;
import com.gerp.dartachalani.dto.kasamu.KasamuResponsePaginationPojo;
import com.gerp.dartachalani.service.PdfGeneratorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.Locale;

@RestController
@RequestMapping("/report")
public class PdfGeneratorController extends BaseController {

    private final PdfGeneratorService pdfGeneratorService;

    public PdfGeneratorController(PdfGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @PostMapping("/darta")
    public ResponseEntity<Resource> generatePdfForDarta(@RequestBody GetRowsRequest paginatedRequest, HttpServletRequest request) {
        byte[] bytes = pdfGeneratorService.generatePdfForInbox(paginatedRequest, "NP");
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= darta-inbox-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @PostMapping("/darta/data")
    @Operation(summary = "Get darta data for report", tags = {"Received-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReceivedLetterResponsePojo.class))})
    public ResponseEntity<?> getDartaReportData(@RequestBody GetRowsRequest paginatedRequest) {

        return ResponseEntity.ok(successResponse("Success", pdfGeneratorService.getDartaReportData(paginatedRequest)));
    }

    @PostMapping("/darta/pdf")
    public ResponseEntity<Resource> getDartaReportPdf(@RequestBody GetRowsRequest paginatedRequest, @RequestParam("lan") String lan) {
        byte[] bytes = pdfGeneratorService.getDartaReportPdf(paginatedRequest, lan);
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= darta-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @GetMapping("/darta/search-recommendation")
    public ResponseEntity<GlobalApiResponse> getDartaSearchRecommendation() {
        return ResponseEntity.ok(successResponse("Success", pdfGeneratorService.getDartaReportSearchRecommendation()));
    }

    @PostMapping("/chalani/data")
    @Operation(summary = "Get chalani data for report", tags = {"Dispatch-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DispatchedResponsePojo.class))})
    public ResponseEntity<?> getChalaniReportData(@RequestBody GetRowsRequest paginatedRequest) {

        return ResponseEntity.ok(successResponse("Success", pdfGeneratorService.getChalaniReportData(paginatedRequest)));
    }

    @PostMapping("/chalani/pdf")
    public ResponseEntity<Resource> getChalaniReportPdf(@RequestBody GetRowsRequest paginatedRequest, @RequestParam("lan") String lan) {
        byte[] bytes = pdfGeneratorService.getChalaniReportPdf(paginatedRequest, lan);
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= chalani-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @GetMapping("/chalani/search-recommendation")
    public ResponseEntity<GlobalApiResponse> getChalaniSearchRecommendation() {
        return ResponseEntity.ok(successResponse("Success", pdfGeneratorService.getChalaniReportSearchRecommendation()));
    }


    @PostMapping("/tippani/data")
    @Operation(summary = "Get tippani data for report", tags = {"Memo-Letter"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponsePojo.class))})
    public ResponseEntity<?> getTippaniReportData(@RequestBody GetRowsRequest paginatedRequest) {

        return ResponseEntity.ok(successResponse("Success", pdfGeneratorService.getTippaniReportData(paginatedRequest)));
    }

    @PostMapping("/tippani/pdf")
    public ResponseEntity<Resource> getTippaniReportPdf(@RequestBody GetRowsRequest paginatedRequest, @RequestParam("lan") String lan) {
        byte[] bytes = pdfGeneratorService.getTippaniReportPdf(paginatedRequest, lan);
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= tippani-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @GetMapping("/tippani/search-recommendation")
    public ResponseEntity<GlobalApiResponse> getTippaniSearchRecommendation() {
        return ResponseEntity.ok(successResponse("Success", pdfGeneratorService.getTippaniReportSearchRecommendation()));
    }


    @PostMapping("/kasamu/data")
    @Operation(summary = "Get Kasamu data for report", tags = {"Kasamu"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = KasamuResponsePaginationPojo.class))})
    public ResponseEntity<?> getKasamuReportData(@RequestBody GetRowsRequest paginatedRequest) {
        return ResponseEntity.ok(successResponse("Success", pdfGeneratorService.getKasamuReportData(paginatedRequest)));
    }

    @PostMapping("/kasamu/pdf")
    public ResponseEntity<Resource> getKasamuReportPdf(@RequestBody GetRowsRequest paginatedRequest, @RequestParam("lan") String lan) {
        byte[] bytes = pdfGeneratorService.getKasamuReportPdf(paginatedRequest, lan);
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= kasamu-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }

    @PostMapping("/chalani")
    public ResponseEntity<Resource> generatePdfForChalani(@RequestBody GetRowsRequest paginatedRequest, HttpServletRequest request) {
        byte[] bytes = pdfGeneratorService.generatePdfFoChalani(paginatedRequest, "NP");
        assert bytes != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= darta-inbox-report.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);
    }


    private String getLanguage(HttpServletRequest request) {
        String currentLanguage = request.getHeader("accept-language");
        if (currentLanguage == null) {
            return "EN";
        } else {
            if (currentLanguage.contains("np")) {
                return "NP";
            } else {
                return "EN";

            }
        }
    }
}
