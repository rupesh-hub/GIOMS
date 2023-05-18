package com.gerp.sbm.Proxy;

import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DateNepali",url = "http://103.69.124.84:9095/api/v1")
public interface NepaliDate {

    @GetMapping("/calendar/2078/04/")
    ResponseEntity<?> getNepaliDate();
}
