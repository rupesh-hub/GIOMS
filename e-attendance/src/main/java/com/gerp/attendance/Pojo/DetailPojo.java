package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailPojo {

    private Double leaveTakenDays;

    private Double totalAllowedDays;

    private Double remainingLeave;

    private String nameNp;

    private String nameEn;

    private String officeCode;

    private Long leaveSetupId;

    private Boolean contractLeave;

    private Long leavePolicyId;

}
