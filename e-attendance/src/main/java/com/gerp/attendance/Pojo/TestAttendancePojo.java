package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestAttendancePojo {
    private Long AttendanceDeviceTypeId;

    private String ClientAlias;

    private Long DeviceMachineNo;

    private String DeviceTypeName;

    private String IPAddress;

    private Long Id;

    private Long Port;

    private Long Status;

    private Long StatusChgUserId;

//    private Long machineNumber;
//
//    private Long Model;
//
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private LocalDate date;
//
//    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
//    private LocalDate checkin;

}
