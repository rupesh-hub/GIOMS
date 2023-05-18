package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttendanceMonthlyReportPojo {
    private long totalLeave;
    private long totalLate;
    private String fullName;
    private long totalUninformedLeave;
    private String employeePosition;
    private String totalLeaveEarlier;
    private long total_kaaj;
    private String piscode;
    private String employeeCode;
}
