package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.tms.pojo.MonthsPojo;
import com.gerp.tms.pojo.authorization.SupportOfficePojo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressReportDetailsPojo  {
    private Long id;
    private String taskId;
    private String taskName;
    private List<MonthsRemarksPojo> monthsRemarks;
    private String deadLine;
    private Double allocatedBudgets;
    private Double budgetExpenditure;
    private String remarks;
    private String employeePisCode;
    private EmployeeMinimalPojo employee;
}
