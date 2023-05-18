package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskProgressStatusResponsePojo {

    private Long id;
    private String statusName;
    private String statusNameNp;
    private int orderStatus;
    private boolean isDeletable;
    private Boolean active;
}
