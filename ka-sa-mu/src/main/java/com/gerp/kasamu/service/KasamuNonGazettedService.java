package com.gerp.kasamu.service;

import com.gerp.kasamu.pojo.request.KasamuForNoGazettedPojo;
import com.gerp.kasamu.pojo.response.TaskResponse;

public interface KasamuNonGazettedService {
    Long addKasamuNonGazetted(KasamuForNoGazettedPojo kasamuForNoGazettedPojo);

    Long updateKasamuNonGazetted(KasamuForNoGazettedPojo kasamuForNoGazettedPojo);

    TaskResponse getKasamuNonGazetted(Long id, String fiscalYear, String evaluationPeriod);

    Long removeKasamuNonGazetted(Long id);
}
