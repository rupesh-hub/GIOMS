package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeAbsentPojo {
    private String pisCode;

    private String employeeNameEn;

    private String employeeNameNp;

    private String designationEn;

    private String designationNp;
}
