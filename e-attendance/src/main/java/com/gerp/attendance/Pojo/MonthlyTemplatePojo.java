package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceMonthlyReportPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MonthlyTemplatePojo {

    private List<String> headers;

    private List<String> monthDays;

    private List<String>headerEmployee;

    private List<EmployeeAttendanceMonthlyReportPojo> eAttMonthReport;
}
