package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class LeaveTakenPojo {
    private Double leaveTaken;

    private Double leaveTakenFy;

    private Double accumulatedLeaveFy;

    private Double accumulatedLeave;

    private Double leaveMonthly;

    private Integer travelDays;

    private Double homeLeave;

    private  Double leaveTakenObsequies;
}
