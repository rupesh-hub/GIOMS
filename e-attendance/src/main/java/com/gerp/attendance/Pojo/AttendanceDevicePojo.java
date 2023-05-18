package com.gerp.attendance.Pojo;

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
public class AttendanceDevicePojo {

    private Integer id;

    private String officeCode;

    private String deviceName;

    private String deviceModel;

    private String deviceSerialNo;

    private Integer port;

    private String ip;

    private LocalDate effectDateEn;

    private String effectDateNp;

    private Integer vendorId;

    private Integer deviceMachineNo;

    private String macAddress;

    private AttendanceDeviceStatus status;


}
