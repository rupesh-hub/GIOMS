package com.gerp.tms.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberWiseProjectResponsePojo {

    private List<ProjectResponsePojo> completedProjects;
    private List<ProjectResponsePojo> inCompletedProjects;
}
