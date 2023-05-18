package com.gerp.tms.pojo.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityLevelPojo  {
    private Integer id;
    private String activityLevelUcd;
    private String activityLevelNameE;
    private String activityLevelNameN;
    private boolean isProject;
    private Integer projectId;
    private List<AuthorizationActivityPojo> authorizationActivity;
}
