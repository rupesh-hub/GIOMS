package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.AttendanceRemarksKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.DayKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceRemarks;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.enums.Day;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeAttendanceReportDataPojo {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dates;

    private String dateNp;

    private int days;

    @JsonSerialize(using = DayKeyValueOptionSerializer.class, as = Enum.class)
    private Day day;

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceStatus attendanceType;

    @JsonSerialize(using = AttendanceRemarksKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceRemarks attendanceRemarks;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkin;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkout;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime shiftCheckin;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime shiftCheckout;

    private String lateCheckin;

    private String earlyCheckout;

    private Long epochTime;

    private String extraTime;

    private String lateRemarks;

    private String employeeNameEn;
    private String employeePisCode;

    private String employeeNameNp;

    private String employeeDesignationEn;

    private String employeeDesignationNp;

    public Day getDay() {
        return Day.values()[this.days];
    }
}
