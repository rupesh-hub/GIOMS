package com.gerp.tms.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectAndTaskStatusPojo {
    private Long projectId;
    private List<String> status;
}
