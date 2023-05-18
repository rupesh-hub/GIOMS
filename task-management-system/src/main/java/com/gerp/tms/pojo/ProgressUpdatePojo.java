package com.gerp.tms.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ProgressUpdatePojo {

    @NotNull
    private Long taskId;
    private Double totalBudget;
    private String remarks;
    private String recordDetails;
    private List<MonthsPojo> progress;

}
