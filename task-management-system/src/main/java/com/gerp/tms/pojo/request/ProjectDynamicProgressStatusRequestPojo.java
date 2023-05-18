package com.gerp.tms.pojo.request;

import com.gerp.tms.pojo.TaskProgressStatusWithOrderStatusPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDynamicProgressStatusRequestPojo {
    @NotNull
    private Integer projectId;

    @NotEmpty
    private Set<TaskProgressStatusWithOrderStatusPojo> tpsIdList;


}
