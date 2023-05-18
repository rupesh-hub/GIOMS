package com.gerp.attendance.Pojo.report;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendanceSummaryDataPojo {
    private String employeeNameEn;

    private String employeeNameNp;

    private String employeeDesignationEn;

    private String employeeDesignationNp;

    private Long workingDays;

    private Long weekendDays;

    private Long holidayCount;

//    @JsonFormat(pattern = "HH:mm")
    private Long totalWorkingHour;

//    @JsonFormat(pattern = "HH:mm")
    private Long workedHours;

    private Long leaveTaken;

    private Long kaaj;

    private Long totalDays;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalIrregularTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalExtraTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalLateCheckin;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalEarlyCheckout;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime totalEarlyCheckin;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime presentInHoliday;
}
