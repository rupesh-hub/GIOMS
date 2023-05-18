package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemainingLeaveMinimalPojo {
    private String pisCode;


    private String pisNameEn;
    private String pisNameNp;


    private List<RemainingLeaveRequestPojo> remainingLeave;

    private EmployeeMinimalPojo employeeDetails;
}
