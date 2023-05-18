package com.gerp.tms.pojo.request;

import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.pojo.authorization.SupportOfficePojo;
import com.gerp.tms.pojo.response.ProjectPhasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/*
 * @project gerp-main
 * @author jitesh

 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestPojo {
    private Integer id;

    private String code;

    private Integer priority;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    private String projectName;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate endDate;

    private String startDateNp;

    private String endDateNp;


    private List<ProjectPhasePojo> phaseList;

    private List<MultipartFile> document;

    private Integer committeeId;
    private Integer officeId;
    private String colorSchema;

    private Boolean requestFromActivity;
    private List<SupportOfficePojo> supportOffices;
    private Integer activityId;
    private String targetOutcomes;
    private String responsibleUnitCode;
}
