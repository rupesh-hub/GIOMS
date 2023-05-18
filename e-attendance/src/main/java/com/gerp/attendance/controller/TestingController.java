//package com.gerp.attendance.controller;
//
//import com.gerp.attendance.service.ExcelService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class TestingController {
//    @Autowired
//    private ExcelService excelService;
//
//    @GetMapping("/")
//    public ResponseEntity<Resource> getFile() {
//        String filename = "manual-attendance.xlsx";
//        InputStreamResource file = new InputStreamResource(excelService.load());
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
//                .contentType(MediaType.parseMediaType("application/xlsx"))
//                .body(file);
//    }
//}
