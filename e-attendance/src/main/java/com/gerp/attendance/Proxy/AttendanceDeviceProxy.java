package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.DevicePojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "AlbumsWebService", url = "https://jsonplaceholder.typicode.com/posts/1")
public interface AttendanceDeviceProxy {

    @GetMapping
    ResponseEntity<?> getAllPost();

}

