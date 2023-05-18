package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceStatus;
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
public class AttendanceShiftPojo {

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceStatus attendanceStatus;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime shiftCheckin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime shiftCheckout;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkout;


}
