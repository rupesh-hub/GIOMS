package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportProjectTaskDetailsPojo {
    private Long taskId;
    private String activityCode;
    private String projectName;
    private String taskName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDateEn;
    private String startDateNp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDateEn;
    private String endDateNp;
    private String status;
    private String workedMonths;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ProgressReportDetailsPojo> progressReportDetails;
}
