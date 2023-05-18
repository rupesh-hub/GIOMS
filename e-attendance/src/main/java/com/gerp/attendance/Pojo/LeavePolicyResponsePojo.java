package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeavePolicyResponsePojo {

    private Long id;

    private Integer totalAllowedDaysFy;

    private Integer totalAllowedDays;

    private Integer totalAllowedRepetition;

    private Integer totalAllowedRepetitionFy;

    private Integer maxAllowedAccumulation;

    private Long leaveSetupId;

    private String leaveNameEn;

    private String leaveNameNp;

    private Integer leaveTaken;

    private Integer remainingLeave;



}
