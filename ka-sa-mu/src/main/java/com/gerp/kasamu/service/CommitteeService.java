package com.gerp.kasamu.service;

import com.gerp.kasamu.pojo.request.CommitteeIndicatorRequestPojo;
import com.gerp.kasamu.pojo.response.CommitteeIndicatorResponsePojo;

import java.util.List;

public interface CommitteeService {
    Long addCommitteeIndicator(CommitteeIndicatorRequestPojo committeeIndicatorRequestPojo);

    Long updateCommitteeIndicator(CommitteeIndicatorRequestPojo committeeIndicatorRequestPojo);

    List<CommitteeIndicatorResponsePojo> getCommitteeIndicator(Long kasamuMasterId, Long committeeIndicatorId);

    void deleteCommitteeIndicator(Long id);
}
