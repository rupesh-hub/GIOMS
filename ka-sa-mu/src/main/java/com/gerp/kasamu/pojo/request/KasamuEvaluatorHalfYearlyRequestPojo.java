package com.gerp.kasamu.pojo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class KasamuEvaluatorHalfYearlyRequestPojo {
    @NotNull
    private Long kasamuMasterId;

    private String valRemarks;
}
