package com.gerp.tms.pojo.response;

/*
 * @project gerp-main
 * @author jitesh

 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectPhasePojo {
    @NotNull
    private Long phaseId;

    private String phaseDescription;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate endDate;

    private String startDateNp;

    private String endDateNp;

    private Integer projectId;

    private Set<String> members;
}
