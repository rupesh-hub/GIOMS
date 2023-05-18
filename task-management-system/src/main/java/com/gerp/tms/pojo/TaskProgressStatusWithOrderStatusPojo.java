package com.gerp.tms.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TaskProgressStatusWithOrderStatusPojo {
    @NotNull
    private Long taskProgressStatusId;
    @NotNull
    private Integer orderStatus;
}
