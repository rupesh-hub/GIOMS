package com.gerp.kasamu.service;

import com.gerp.kasamu.pojo.request.KasamuDetailRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuDetailResponsePojo;
import com.gerp.kasamu.pojo.response.TaskResponse;

import java.util.List;

public interface KasamuDetailService {
    Long addKasamuDetail(KasamuDetailRequestPojo kasamuDetailRequestPojo);

    Long updateKasamuDetails(KasamuDetailRequestPojo kasamuDetailRequestPojo);

    TaskResponse getKasamuDetails(Long id, String fiscalYear, String evaluationPeriod);

    void removeKasamuDetails(Long id);
}
