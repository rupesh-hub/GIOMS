package com.gerp.tms.pojo.authorization;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ActivityTOProject{
    private int activityId;
    private String color;
    private int priority;
    private LocalDate startDateE;
    private String startDateN;
    private LocalDate endDateE;
    private String endDateN;

}
