package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveRequestUpdatePojo extends RequestDayPojo {

    @NotNull
    private Long id;
    private Long leavePolicyId;
    private Long periodicHoliday;
    private String approverPisCode;
    private String appliedPisCode;
    private Boolean isApprover;
    private String leaveRequesterHashContent;
    private String leaveRequesterSignature;
    private String leaveRequesterContent;
    private String content;
    private Boolean appliedForOthers;
    private List<Long> documentsToRemove;

}
