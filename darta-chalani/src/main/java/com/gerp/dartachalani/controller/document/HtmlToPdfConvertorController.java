package com.gerp.dartachalani.controller.document;

import com.gerp.dartachalani.dto.FileConverterPojo;
import com.gerp.dartachalani.service.FileConvertService;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/convert")
public class HtmlToPdfConvertorController {

    private final FileConvertService fileConvertService;

    public HtmlToPdfConvertorController(FileConvertService fileConvertService) {
        this.fileConvertService = fileConvertService;
    }

    @PostMapping("/pdf")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> convertToPdf(@RequestBody FileConverterPojo fileConverterPojo, HttpServletRequest request) {
        byte[] bytes = fileConvertService.convertToPdf1(fileConverterPojo);
        assert bytes != null;
        ByteArrayResource file = new ByteArrayResource(bytes);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= darta-inbox-report.html")
                .contentType(MediaType.TEXT_HTML)
                .contentLength(file.contentLength())
                .body(file);
    }

    @GetMapping("/html")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> downloadHtml() {
        byte[] bytes = fileConvertService.convertToPdf1(new FileConverterPojo());
        assert bytes != null;
        ByteArrayResource file = new ByteArrayResource(bytes);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= letter.html")
                .contentType(MediaType.TEXT_HTML)
                .contentLength(file.contentLength())
                .body(file);
    }
}
