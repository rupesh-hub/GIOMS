package com.gerp.kasamu.pojo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class CommitteeIndicatorRequestPojo {

    private Long id;

    @NotNull
    private String capacityType;

    @NotNull
    private Double scoredMarks;

    @NotNull
    private String scoredLevel;


    private Long kasamuMasterId;

}
