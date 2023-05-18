package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetail {

    private String employeeName;

    private String employeeCode;

    private String organizationName;

    private String birthDate;

    private String joinDate;

    private String district;

}
