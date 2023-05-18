package com.gerp.tms.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskRatingPojo {

    @NotNull
    private Long taskId;

    @NotNull
    private Integer rating;

    private String description;
}
