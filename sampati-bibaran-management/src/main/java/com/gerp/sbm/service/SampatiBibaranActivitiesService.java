package com.gerp.sbm.service;

import com.gerp.sbm.model.sampati.SampatiMaster;
import com.gerp.sbm.model.sampati.SampatiViewRequest;

import java.util.List;

public interface SampatiBibaranActivitiesService {
    Boolean addrequest(String piscode);
    List<SampatiViewRequest> getReviewRequest();

    Boolean approvedRequest(String requester_piscode);

    SampatiMaster getSampatiBibaran(String piscode,String fiscal_year_code);
}
