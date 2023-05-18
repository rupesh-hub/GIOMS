package com.gerp.attendance.controller.Attendance;

import com.gerp.attendance.Pojo.attendance.RealTimeAttPojo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("device-test")
@Slf4j
public class DeviceTestController {
    
    @PostMapping("hit-check")
    public ResponseEntity<?> hitCheck(@RequestBody RealTimeAttPojo realTimeAttPojo) {
        log.info(new Gson().toJson(realTimeAttPojo));
        return ResponseEntity.ok(realTimeAttPojo);
    }

    @PostMapping("hit-check-auth")
    public ResponseEntity<?> hitCheckAuth(@RequestBody RealTimeAttPojo realTimeAttPojo) {
        log.info(new Gson().toJson(realTimeAttPojo));
        return ResponseEntity.ok(realTimeAttPojo);
    }

}