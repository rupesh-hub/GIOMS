package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendanceTotalSum {

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
