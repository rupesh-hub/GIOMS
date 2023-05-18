package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttendanceAnnualReportPojo {

    @JsonFormat(pattern = "hh:mm")
    private LocalTime checkin;

    @JsonFormat(pattern = "hh:mm")
    private LocalTime checkout;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;
    private String officeCode;
    private String pisCode;
    private String message;

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceStatus attendanceStatus;
}
