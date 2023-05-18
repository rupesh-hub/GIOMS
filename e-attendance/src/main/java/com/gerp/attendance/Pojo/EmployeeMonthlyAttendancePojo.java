package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceNewMonthlyPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeMonthlyAttendancePojo {
    private String month;

    private List<EmployeeAttendanceNewMonthlyPojo> monthlyData;
}
