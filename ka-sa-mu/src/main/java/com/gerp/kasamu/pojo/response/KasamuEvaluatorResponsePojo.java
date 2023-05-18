package com.gerp.kasamu.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KasamuEvaluatorResponsePojo {

    private Long id;
    private String valuatorType;
    private String type;
    private String fullMarks;
    private String scoredMarks;
    private String scoredLevel;
    private String kasamuEvaluatorEn;

}
