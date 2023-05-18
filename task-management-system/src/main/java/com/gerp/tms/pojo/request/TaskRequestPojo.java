package com.gerp.tms.pojo.request;

import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.pojo.TaskMembersPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestPojo {

    private Long id;

    private Long code;

    @NotNull
    private String taskName;

    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String taskDescription;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String priority;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate endDate;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String startDateNp;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String endDateNp;

    @NotNull
    private Integer projectId;

    @NotNull
    private Long phaseId;

    private List<MultipartFile> document;

    @NotNull
    private List<TaskMembersPojo> members;

}
