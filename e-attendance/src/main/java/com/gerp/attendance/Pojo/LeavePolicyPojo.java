package com.gerp.attendance.Pojo;

import com.gerp.shared.enums.Gender;
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
public class LeavePolicyPojo {


    private Long id;

    private String officeCode;

    private Long leaveSetupId;

//    private String leaveNameEn;
//
//    private String leaveNameNp;
//
//    private Integer totalAllowedAccumulation;
//
//    private Integer totalAllowedAccumulationFy;

    private String year;

    private Integer maxAllowedAccumulation;


    private Integer totalAllowedDays;

    private Integer allowedDaysFy;

    private Integer totalAllowedRepetition;

    private Integer totalAllowedRepetitionFy;

    private Integer gracePeriod;

    private Integer documentSubmissionDay;

    private Integer leaveApprovalDays;

    private Integer maximumLeaveLimitAtOnce;

    private Integer minimumYearOfServices;

    private Integer allowedLeaveMonthly;

    private Gender gender;

    private Boolean contractLeave;

    private Boolean permissionForApproval;

    private Boolean carryForward;

//    private Boolean countWeekend;

    private Boolean countPublicHoliday;

//    private Boolean allowLumpSum;
//
//    private Boolean allowDeduction;

    private Boolean allowHalfLeave;

    private Boolean paidLeave;

    private Boolean allowSubstitution;

    /* for pojo from leavepolicyMapper*/
    private Boolean validateRequest;

//    private Boolean needRecommendation;
//
//    private Boolean needApproval;

}
