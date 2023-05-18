package com.gerp.kasamu.service;

import com.gerp.kasamu.pojo.KasamuRequestReviewListPojo;
import com.gerp.kasamu.pojo.request.KasamuMasterRequestPojo;
import com.gerp.kasamu.pojo.request.RequestForViewPojo;

import java.util.List;

public interface KasamuRequestForViewService {
     Long requestForView(RequestForViewPojo pisCode);

     List<KasamuRequestReviewListPojo> getRequestedList();

     Long decisionGivenBy(Long id, Boolean decision);
}
