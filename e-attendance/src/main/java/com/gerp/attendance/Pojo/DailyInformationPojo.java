package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyInformationPojo {

    private String pisCode;
    private String employeeCode;
    private String durationType;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime inTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime outTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime halfTime;


    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime closeTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceStatus status;

    private String empNameEn;
    private String empNameNp;

    private String sectionNameEn;
    private String sectionNameNp;

    private String fdNameEn;
    private String fdNameNp;

    private EmployeeMinimalPojo employeeDetails;

    private Boolean presentHoliday;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime lateCheckin;

    private String actualLateCheckIn;

    private String actualEarlyCheckOut;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime earlyCheckout;

    private Long positionOrderNo;




}
