package com.gerp.kasamu.pojo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
public class KasamuCommitteePojoRequest {

    @NotNull
    private Long kasamuMasterId;

    @NotNull
    private Set<CommitteeRequestPojo> committeeMembers;

    @NotNull
    private Set<CommitteeIndicatorRequestPojo> committeeReviewList;
}
