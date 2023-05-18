package com.gerp.tms.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCountPojo {
    private int completedProject;
    private int notStartedProject;
    private int inProgressProject;
}
