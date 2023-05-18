package com.gerp.tms.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class PhaseMemberPojo {

    @NotNull
    private Integer projectId;
    @NotNull
    private Long phaseId;
    @NotNull
    private Set<String> members;
}
