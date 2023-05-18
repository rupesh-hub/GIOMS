package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResponsePojo implements Serializable {

    @JsonProperty("ResultType")
    private Long resultType;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("ReturnId")
    private Long  returnId;
    @JsonProperty("dataRow")
    private List<TestResultPojo> dataRow;

}


