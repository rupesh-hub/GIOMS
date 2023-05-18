package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.tms.pojo.document.DocumentResponsePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponsePojo {

    private Integer id;

    private String code;

    private Integer priority;

    private String projectName;

    private Boolean isCommittee;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String startDateNp;

    private String endDateNp;

    private Boolean isResponded;

    private String status;

    private Integer responseBy;

    private List<ProjectPhaseResponsePojo> phaseList;

    private List<DocumentResponsePojo> documents;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private CommitteeResponsePojo committee;

    private List<TaskProgressStatusResponsePojo> taskProgressStatusList;

    private Boolean isCompleted;

    private String officeId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate completedDate;

    private Integer createdBy;
    private String activityCode;

    private boolean isBookedMarked;

    private String colorSchema;

    private Boolean active;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String>  taskStatus;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TaskResponsePojo>  tasks;
}
