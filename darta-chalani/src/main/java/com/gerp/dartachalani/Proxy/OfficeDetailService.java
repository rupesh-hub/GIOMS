package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.dto.document.DocumentMasterResponsePojo;
import feign.Headers;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(name = OfficeDetailService.SERVICE_NAME, url = OfficeDetailService.SERVICE_URL )
@RibbonClient(name = OfficeDetailService.SERVICE_NAME)
public interface OfficeDetailService {
    String SERVICE_NAME = "d";
    String SERVICE_URL = "http://103.69.124.84:9094";

    @GetMapping("/office/details ")
    @Headers("Content-Type: multipart/form-data")
    ResponseEntity<DocumentMasterResponsePojo> create(@RequestParam String officeCode);

}
