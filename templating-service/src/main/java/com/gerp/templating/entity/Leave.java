package com.gerp.templating.entity;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Leave {
    private String publicHoliday;

    private String homeLeave;

    private String sickLeave;

    private String paternityLeave;

    private String studyLeave;

    private String emergencyLeave;

    private String unpaidLeave;
}
