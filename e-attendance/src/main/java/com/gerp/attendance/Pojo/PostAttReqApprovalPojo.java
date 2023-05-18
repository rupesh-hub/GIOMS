package com.gerp.attendance.Pojo;

import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAttReqApprovalPojo {

    private Long id;
    private Status status;
    private String approverPisCode;
    private String remarks;
    private Long attendanceDetailId;

}
