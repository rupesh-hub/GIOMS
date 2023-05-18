package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.tms.pojo.TaskMembersPojo;
import com.gerp.tms.pojo.document.DocumentPojo;
import com.gerp.tms.pojo.response.TaskProgressStatusResponsePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponsePojo {

    private Long id;
    private String code;
    private String taskName;
    private String taskDescription;
    private String priority;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String startDateNp;
    private String endDateNp;
    private String projectId;
    private Boolean isResponded;
    private Integer responseBy;
    private String taskStatus;
    private List<DocumentPojo> documents;
    private List<TaskMembersPojo> taskMembers;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    private TaskProgressStatusResponsePojo taskProgressStatus;
    private Integer createdBy;
    private Integer rating;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate completedDate;

}
