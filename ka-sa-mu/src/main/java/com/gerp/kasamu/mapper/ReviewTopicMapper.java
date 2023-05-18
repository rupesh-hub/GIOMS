package com.gerp.kasamu.mapper;

import com.gerp.kasamu.pojo.response.KasamuReviewTopicsResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ReviewTopicMapper {
    List<KasamuReviewTopicsResponsePojo> getReviewTopic( @Param("reviewType") String reviewType, @Param("kasamuClass") String kasamuClass);

    KasamuReviewTopicsResponsePojo getReviewTopicById(@Param("id") Long id);
}
