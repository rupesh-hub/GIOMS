package com.gerp.kasamu.service;

import com.gerp.kasamu.pojo.request.KasamuReviewTopicsRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuReviewTopicsResponsePojo;

import java.util.List;

public interface ReviewTopicService {
    Long addReviewTopic(KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo);

    Long updateReviewTopic(KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo);

    List<KasamuReviewTopicsResponsePojo> getReviewTopic(String pisCode, String kasamuClass);

    KasamuReviewTopicsResponsePojo getReviewTopicById(Long id);
}
