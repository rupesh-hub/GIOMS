package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.EmployeeMonthlyAttendancePojo;
import com.gerp.attendance.Pojo.LeaveReportDataPojo;
import com.gerp.attendance.Pojo.RemainingReportPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeAttendanceMonthlyReportPojo {

    private String sn;
    private String pisCode;
    private String employeeCode;
    public String language;

    private String empNameEn;
    private String empNameNp;

    private String fdNameEn;
    private String fdNameNp;

    private Boolean isActive;

    private Boolean isLeft=Boolean.FALSE;
    private Boolean isJoin=Boolean.FALSE;

    private List<String> shortNameNp;

    private List<MonthDataPojo> monthlyAttendanceData;
    private DesignationDataPojo designationData;
    private List<MonthDataLeavePojo> monthlyLeaveData;
    private List<LeaveReportDataPojo> leaveReportDataPojoList;


    private List<MonthDataKaajPojo> monthlyKaajData;
    private List<EmployeeAttendanceNewMonthlyPojo> monthlyAttendanceStatus;
    private List<EmployeeMonthlyAttendancePojo> monthlyAttendance;
    private List<RemainingReportPojo> remainingReportPojos;

    private List<MonthlyDailyLog> monthlyDailyLogs;
}
