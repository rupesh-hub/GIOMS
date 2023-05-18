package com.gerp.kasamu.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.kasamu.pojo.request.KasamuForNoGazettedPojo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskResponse {
    private Boolean isFiscalYearSubmitted;
    private List<KasamuDetailResponsePojo> kasamuDetailResponsePojoList;
    private List<KasamuForNoGazettedPojo> kasamuForNoGazettedPojos;
}
