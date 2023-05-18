package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AttendanceReportType {
    MONTHLY, ANNUAL
}
