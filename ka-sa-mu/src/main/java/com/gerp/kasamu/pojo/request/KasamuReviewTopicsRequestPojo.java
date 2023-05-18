package com.gerp.kasamu.pojo.request;

import com.gerp.kasamu.constant.ReviewConstant;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class KasamuReviewTopicsRequestPojo  {

    private Long id;

    @NotNull
    private String description;

    private String kasamuClass;

    @NotNull
    private ReviewConstant reviewType;
}
