package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatePojo {
    Date fromDate;
    Date toDate;
    AttendanceReportType attendanceReportType;
}
