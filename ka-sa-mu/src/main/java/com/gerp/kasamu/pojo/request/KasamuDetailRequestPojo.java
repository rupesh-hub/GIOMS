package com.gerp.kasamu.pojo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class KasamuDetailRequestPojo {

    private Long id;
    @NotNull
    private String taskType;

    @NotNull
    private String task;

    @NotNull
    private String estimation;

    @NotNull
    private Double semiannualTarget;

    @NotNull
    private Double annualTarget;

    @NotNull
    private Double achievement;

    private String remarks;
}
