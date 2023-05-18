package com.gerp.kasamu.service.impl;

import com.gerp.kasamu.Proxy.EmployeeDetailsProxy;
import com.gerp.kasamu.constant.ErrorMessages;
import com.gerp.kasamu.constant.ReviewConstant;
import com.gerp.kasamu.mapper.ReviewTopicMapper;
import com.gerp.kasamu.model.kasamu.KasamuReviewTopics;
import com.gerp.kasamu.pojo.EmployeePojo;
import com.gerp.kasamu.pojo.request.KasamuReviewTopicsRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuReviewTopicsResponsePojo;
import com.gerp.kasamu.repo.ReviewTopicRepository;
import com.gerp.kasamu.service.ReviewTopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewTopicServiceImpl implements ReviewTopicService {
    private final ReviewTopicRepository reviewTopicRepository;
    private final ReviewTopicMapper reviewTopicMapper;
    private final EmployeeDetailsProxy employeeDetailsProxy;

    public ReviewTopicServiceImpl(ReviewTopicRepository reviewTopicRepository, ReviewTopicMapper reviewTopicMapper, EmployeeDetailsProxy employeeDetailsProxy) {
        this.reviewTopicRepository = reviewTopicRepository;
        this.reviewTopicMapper = reviewTopicMapper;
        this.employeeDetailsProxy = employeeDetailsProxy;
    }

    @Override
    public Long addReviewTopic(KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo) {
        KasamuReviewTopics reviewTopics = new KasamuReviewTopics();
        toReviewTopicEntity(kasamuMasterRequestPojo,reviewTopics);
        reviewTopicRepository.save(reviewTopics);
        return reviewTopics.getId();
    }

    @Override
    public Long updateReviewTopic(KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo) {
        if (kasamuMasterRequestPojo.getId() == null){
            throw  new RuntimeException(ErrorMessages.ID_IS_MISSING.getMessage());
        }
        KasamuReviewTopics kasamuReviewTopics = getReviceTopic(kasamuMasterRequestPojo);
        toReviewTopicEntity(kasamuMasterRequestPojo,kasamuReviewTopics);
        reviewTopicRepository.save(kasamuReviewTopics);
        return kasamuReviewTopics.getId();
    }

    @Override
    public List<KasamuReviewTopicsResponsePojo> getReviewTopic(String pisCode, String reviewType) {
        EmployeePojo employeePojo = employeeDetailsProxy.getEmployeeDetail(pisCode);
        return reviewTopicMapper.getReviewTopic(reviewType,employeePojo.getCoreDesignation().getName());
    }

    @Override
    public KasamuReviewTopicsResponsePojo getReviewTopicById(Long id) {
        return reviewTopicMapper.getReviewTopicById(id);
    }

    private KasamuReviewTopics getReviceTopic(KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo) {
        Optional<KasamuReviewTopics> kasamuReviewTopicsOptional = reviewTopicRepository.findById(kasamuMasterRequestPojo.getId());
        if (!kasamuReviewTopicsOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.TOPIC_NOT_FOUND.getMessage());
        }
        return kasamuReviewTopicsOptional.orElse(new KasamuReviewTopics());
    }

    private void toReviewTopicEntity(KasamuReviewTopicsRequestPojo kasamuMasterRequestPojo, KasamuReviewTopics reviewTopics) {
        if (kasamuMasterRequestPojo.getReviewType().equals(ReviewConstant.SUPERVISOR)){
            reviewTopics.setKasamuClass("null");
        }else {
            reviewTopics.setKasamuClass(kasamuMasterRequestPojo.getKasamuClass());
        }
        reviewTopics.setReviewType(kasamuMasterRequestPojo.getReviewType().name());
        reviewTopics.setDescription(kasamuMasterRequestPojo.getDescription());

    }
}
