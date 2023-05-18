package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestDaysPojo {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    private Long leavePolicyId;

    private Integer leaveFor;

    private String leaveCause;
}
