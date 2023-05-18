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
public class RamanaTemplate {

    private String departmentName;

    private String patroNo;

    private String date;

    private OfficeDetail officeDetail;

    private EmployeeDetail employeeDetail;

    private DesignationDetail sabikDetail;

    private DesignationDetail saruwaDetail;

    private Leave leaveTaken;

    private Leave accumulatedLeave;

    private Boolean barbujhanathGareko;

    private Boolean barbujhanathNagareko;

    private String ramanaDate;

    private String verifyDate;

    private String salary;

    private String increaseSalary;

    private String allowanceDate;

    private String accumulatedSalary;

    private String expenditureInHealth;

    private RamanaDetail ramanaDetail;


}
