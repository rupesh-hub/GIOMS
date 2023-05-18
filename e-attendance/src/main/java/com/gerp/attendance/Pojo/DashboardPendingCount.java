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
public class DashboardPendingCount {

    private Long totalApproval;

    private Long leaveApproval;

    private Long kaajApproval;

    private Long dailyLogApproval;

    private Long manualAttendanceApproval;




}
