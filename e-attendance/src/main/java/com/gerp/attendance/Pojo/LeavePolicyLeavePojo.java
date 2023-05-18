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
public class LeavePolicyLeavePojo {

    private Long leavePolicyId;

    private String leaveNameEn;

    private String leaveNameNp;

    private Double leaveTaken;


}
