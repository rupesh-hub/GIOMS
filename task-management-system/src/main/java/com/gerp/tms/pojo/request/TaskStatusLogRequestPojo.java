package com.gerp.tms.pojo.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskStatusLogRequestPojo {
    private Long taskId;
    private Long taskProgressStatusId;
    private Integer projectId;
    private String remarks;
}
