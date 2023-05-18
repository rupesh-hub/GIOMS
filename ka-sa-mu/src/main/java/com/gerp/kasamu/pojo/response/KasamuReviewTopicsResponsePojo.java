package com.gerp.kasamu.pojo.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class KasamuReviewTopicsResponsePojo {

    private Long id;

    private String description;

    private String kasamuClass;

    private String reviewType;
}
