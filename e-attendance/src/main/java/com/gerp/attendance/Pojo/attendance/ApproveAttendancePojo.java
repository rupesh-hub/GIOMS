package com.gerp.attendance.Pojo.attendance;

import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.enums.DurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author info
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApproveAttendancePojo {
    private String pisCode;
    private String officeCode;
    private LocalDate fromDateEn;
    private LocalDate toDateEn;
    private Integer travelDays;
    private DurationType durationType;
    private AttendanceStatus attendanceStatus;
    private Boolean isHoliday;
}
