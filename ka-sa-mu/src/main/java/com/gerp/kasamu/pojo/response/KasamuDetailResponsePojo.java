package com.gerp.kasamu.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KasamuDetailResponsePojo {

    private Long id;
    private String taskType;
    private String task;
    private String estimation;
    private String semiannualTarget;
    private String annualTarget;
    private String achievement;
    private String remarks;
    private String kasamuDetailEn;
}
