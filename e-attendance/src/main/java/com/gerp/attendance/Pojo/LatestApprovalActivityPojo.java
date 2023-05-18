package com.gerp.attendance.Pojo;

import com.gerp.attendance.Pojo.approvalActivity.ApprovalActivityPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestApprovalActivityPojo {
    private List<ApprovalActivityPojo> approvalActivityPojoList;
    private VerificationInformation requesterSignature;
    private String remarks;
    private String dateNp;
    private Boolean appliedForOthers;
}
