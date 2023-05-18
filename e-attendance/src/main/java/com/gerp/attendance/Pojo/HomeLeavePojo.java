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
public class HomeLeavePojo {
    private Double homeLeaveAccumulated;

    private Double additionalLeave;

    private Double remaingLeave;

    private Double accumulatedLeave;

    private Integer unInformLeave;

}
