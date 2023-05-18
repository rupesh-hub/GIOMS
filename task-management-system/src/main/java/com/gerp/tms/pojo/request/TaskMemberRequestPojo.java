package com.gerp.tms.pojo.request;

import com.gerp.tms.pojo.TaskMembersPojo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class TaskMemberRequestPojo {
    @NotNull
    private Long taskId;
    @NotNull
    private List<TaskMembersPojo> taskMembers;
}
