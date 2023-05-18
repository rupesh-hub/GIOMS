package com.gerp.tms.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommitteeWiseProjectResponsePojo extends CommitteeResponsePojo {

    private List<ProjectResponsePojo> projectResponsePojoList;
}
