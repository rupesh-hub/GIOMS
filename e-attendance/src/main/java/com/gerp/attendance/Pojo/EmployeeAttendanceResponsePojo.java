package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendanceResponsePojo {

    private Long totalPresent;
    private Long totalLeave;
    private List<EmployeeAttendancePojo> employeeAttendancePojoList;

}
