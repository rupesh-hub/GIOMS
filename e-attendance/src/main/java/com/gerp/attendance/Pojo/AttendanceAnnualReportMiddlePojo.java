package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceAnnualReportMiddlePojo {
    private String month;
    private List<AttendanceAnnualReportPojo> monthlyData;
}
