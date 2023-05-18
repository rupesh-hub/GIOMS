package com.gerp.tms.pojo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DecisionByOfficeHeadPojo {
    @NotNull
    private int id;
    private Boolean status;
    private String remarks;
}
