package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.tms.pojo.authorization.SupportOfficePojo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MonitoringReportsPojo {
    private String projectId;
    private String projectName;
    private String activityCode;
    private String expectedOutcomes;
    private String responsibleUnit;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SupportOfficePojo> supportingOffices;
    private List<ProgressReportDetailsPojo> progressReportDetails;
}
