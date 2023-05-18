package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemainingMonthlyLeavePojo {

    private Date createdDate;

    private Long leavePolicyId;

    private Double accumulatedLeaveFy;

    private Double leaveTaken;

    private Double monthlyLeaveTaken;

    private Integer repetition;
}
