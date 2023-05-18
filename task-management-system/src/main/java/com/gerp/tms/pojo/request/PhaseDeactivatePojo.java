package com.gerp.tms.pojo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PhaseDeactivatePojo {
    @NotNull
    private Integer projectId;
    @NotNull
    private Long phaseId;
    @NotNull
    private boolean active;
}
