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
public class SaruwaTemplate {
    private String department;

    private String date;

    private String duration;

    private DesignationDetail sabikDetail;

    private DesignationDetail saruwaDetail;

    private EmployeeDetail employeeDetail;
}
