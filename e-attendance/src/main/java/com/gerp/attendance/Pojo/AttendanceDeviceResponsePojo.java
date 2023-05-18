package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.enums.AttendanceDeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDeviceResponsePojo {

    private Long id;
    private String officeCode;
    private String deviceName;
    private String deviceMachineNo;
    private String deviceModel;
    private String deviceSerialNo;
    private Integer port;
    private String ip;

    private String macAddress;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectDateEn;
    private String effectDateNp;
    private Long vendorId;
    private String vendorName;
    private Boolean isActive;
    private AttendanceDeviceStatus status;
}
