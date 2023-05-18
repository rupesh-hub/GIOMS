package com.gerp.attendance.util;

import com.gerp.attendance.Pojo.AttendanceDeviceResponsePojo;
import com.gerp.attendance.Pojo.TestResultPojo;
import com.gerp.attendance.Proxy.AttendanceDeviceServiceData;
import com.gerp.attendance.mapper.AttendanceDeviceMapper;
import com.gerp.attendance.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AttendanceDeviceUtil {
    private final AttendanceDeviceServiceData attendanceDeviceServiceData;
    private final AttendanceDeviceMapper attendanceDeviceMapper;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    public AttendanceDeviceUtil(AttendanceDeviceServiceData attendanceDeviceServiceData, AttendanceDeviceMapper attendanceDeviceMapper) {
        this.attendanceDeviceServiceData = attendanceDeviceServiceData;
        this.attendanceDeviceMapper=attendanceDeviceMapper;
    }

    public List<TestResultPojo> saveEmployeeAttendance(String officeCode) {
        Map<String, Object> body
                = new HashMap<>();

//        AttendanceDeviceResponsePojo attendanceDeviceResponsePojo=attendanceDeviceMapper.getByDeviceByOfficeCode("103339","01");

        // TODO get active office and process for each office later
        List<AttendanceDeviceResponsePojo> data=attendanceDeviceMapper.getByDeviceByOfficeCode(officeCode);

        if(data.isEmpty())
            return Collections.emptyList();
        List<TestResultPojo> finalData = new ArrayList<>();
        data.forEach(attendanceDeviceResponsePojo->{
            body.put("AttendanceDeviceTypeId", attendanceDeviceResponsePojo.getVendorId());
            body.put("ClientAlias", "Admin");
            body.put("DeviceMachineNo", attendanceDeviceResponsePojo.getDeviceMachineNo());
            body.put("DeviceTypeName", attendanceDeviceResponsePojo.getVendorName());
            body.put("IPAddress", attendanceDeviceResponsePojo.getIp());
            body.put("Id", attendanceDeviceResponsePojo.getId());
            body.put("Port", attendanceDeviceResponsePojo.getPort());
            body.put("Status", attendanceDeviceResponsePojo.getStatus().getEnum().getValue());
            body.put("StatusChgUserId", 0);
            List<TestResultPojo> testResultPojo = new ArrayList<>();
            try{
                testResultPojo = attendanceDeviceServiceData.create(body);
            }catch (Exception e){
                e.printStackTrace();
            }
            finalData.addAll(testResultPojo);
        });
        return finalData;
    }
}
