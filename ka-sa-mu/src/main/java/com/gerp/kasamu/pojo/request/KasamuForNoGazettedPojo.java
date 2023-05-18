package com.gerp.kasamu.pojo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KasamuForNoGazettedPojo  {

    private Long id;
    private String taskType;
    private String description;
    private String cost;
    private String timeTaken;
    private String quality;
    private String quantity;
    private String remarks;
    private String kasamuNonGazettedEn;

}
