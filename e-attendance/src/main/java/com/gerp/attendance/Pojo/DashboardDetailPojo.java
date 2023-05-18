package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.attendance.Pojo.shift.AttendanceShiftPojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.Pojo.shift.ShiftStatusPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDetailPojo {

    private List<AppliedLeavePojo> appliedLeaveList;

    private Long totalEmployees;

    private Long presentEmployees;

    private Long absentEmployees;

    private Long employeeOnLeave;

    private Long employeeOnKaaj;

    private HolidayResponsePojo upcommingHoliday;

    private Long totalApprovalLeave;

    private Long totallApprovalKaaj;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate todayDateEn;

    private String todayDateNp;

    private ShiftStatusPojo shiftStatus;

    Long kararCount;

}
