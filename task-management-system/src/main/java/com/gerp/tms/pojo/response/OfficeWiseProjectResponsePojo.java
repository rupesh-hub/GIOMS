package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficeWiseProjectResponsePojo {
    private String officeId;
    private int projectCount;
    private List<ProjectResponsePojo> completedProject;
    private List<ProjectResponsePojo> incompleteProject;
}
