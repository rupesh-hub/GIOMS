package com.gerp.kasamu.pojo.request;

import com.gerp.kasamu.pojo.NonGazTipadi;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class KasamuEvaluatorRequestPojo {

    private Long id;

    @NotNull
    private String type;

    @NotNull
    private Double fullMarks;

    @NotNull
    private Double scoredMarks;

    @NotNull
    private String scoredLevel;

}
