package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.kasamu.KasamuReviewTopics;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewTopicRepository extends GenericSoftDeleteRepository<KasamuReviewTopics,Long> {
    boolean existsByKasamuClassAndReviewType(String s, String committee);

}
