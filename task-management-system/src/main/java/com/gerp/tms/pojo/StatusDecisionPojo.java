package com.gerp.tms.pojo;


import com.gerp.tms.constant.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class StatusDecisionPojo {

    @NotNull
    private Integer id;


    @NotNull
    private Status status;

    @NotNull
    private Integer approvedBy;
}
