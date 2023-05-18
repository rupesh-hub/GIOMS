package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommitteeResponsePojo {

    private Integer id;
    private String committeeName;
    private String committeeNameNp;
}
