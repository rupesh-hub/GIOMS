package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceStatusPojo {

    private Integer month;

    @JsonFormat(pattern = "hh:mm")
    private LocalTime checkin;

    @JsonFormat(pattern = "hh:mm")
    private LocalTime checkout;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;
    private String message;

    @JsonIgnore
    private String pisCode;

    private int leaveFlag;

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceStatus attendanceStatus;

}
