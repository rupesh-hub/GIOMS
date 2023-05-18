package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.MonthlyAttendanceKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.enums.MonthlyAttendanceStatus;
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
public class MonthDataPojo {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;

    private Boolean isHoliday;

    private String shortNameEn;
    private String shortNameNp;

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceStatus attendanceStatus;

//    @JsonSerialize(using = MonthlyAttendanceKeyValueOptionSerializer.class, as = Enum.class)
//    private MonthlyAttendanceStatus monthlyAttendanceStatus;

    private Boolean isPresent;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkin;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkout;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime shiftCheckin;


    private String lateRemarks;

    public Boolean getIsPresent() {
        return attendanceStatus.getIsPresent();
    }
}
