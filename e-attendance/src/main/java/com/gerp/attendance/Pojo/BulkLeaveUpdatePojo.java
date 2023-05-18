package com.gerp.attendance.Pojo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkLeaveUpdatePojo {

    private Long id;
    private Long leavePolicyId;
    private String approverPisCode;
    private String appliedPisCode;
    private Boolean isApprover;
    private String leaveRequesterHashContent;
    private String leaveRequesterSignature;
    private String leaveRequesterContent;
    private String content;
    private String description;

    private List<RequestDayPojo> requestLeaves;

}
