package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.TestPojo;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AttendanceDeviceProxyService extends BaseController {
    private final AttendanceDeviceProxy attendanceDeviceProxy;

    public AttendanceDeviceProxyService(AttendanceDeviceProxy attendanceDeviceProxy) {
        this.attendanceDeviceProxy = attendanceDeviceProxy;
    }

    @SneakyThrows
    public TestPojo findAllPosts() {
        ResponseEntity responseEntity = attendanceDeviceProxy.getAllPost();
        System.out.println("checking data in service"+attendanceDeviceProxy.getAllPost());
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS)
            return (TestPojo) globalApiResponse.getData();
        else {
            return null;
        }
    }





}

