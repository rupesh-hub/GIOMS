package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalReportDetailPojo {

    private Long id;
    private EmployeeMinimalPojo employee;
    private String projectName;
    private String taskName;
    private Long taskId;
    private String status;
    private String activityCode;
    private String empPiscode;
}
