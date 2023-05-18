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
public class PisLeaveDetailPojo {

    private String pisCode;

    private Double totalLeaveTaken;

    private Double remainingLeave;

    private Double totalAllowedLeave;
}
