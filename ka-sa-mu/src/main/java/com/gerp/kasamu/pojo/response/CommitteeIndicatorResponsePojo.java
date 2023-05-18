package com.gerp.kasamu.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommitteeIndicatorResponsePojo {

    private Long id;
    private String capacityType;
    private String scoredMarks;
    private String scoredLevel;
    private String committeeIndicatorEn;
}
