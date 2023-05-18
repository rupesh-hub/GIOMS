package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceAnnualReportMainPojo {
    private String empNameEn;
    private String empNameNp;
    private String fdNameEn;
    private String fdNameNp;

    private List<AttendanceAnnualReportMiddlePojo> monthlyAttendance;
}
