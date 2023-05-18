package com.gerp.attendance.Pojo;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveSetupPojo {

    private Long id;

    private String nameEn;

    private String nameNp;

    private String shortNameNp;
    private String shortNameEn;
    private String officeCode;

    private Boolean maximumAllowedAccumulation;

    private Boolean unlimitedAllowedAccumulation;

    private Boolean totalAllowedDays;

    private Boolean totalAllowedRepetition;

    private Boolean leaveApprovalDays;

    private Boolean maximumLeaveLimitAtOnce;

    private Boolean gracePeriod;

    private Boolean isActive;

    private Boolean allowedDaysFy;

    private Boolean totalAllowedRepetitionFy;

    private Boolean documentationSubmissionDay;

    private Boolean minimumYearOfServices;

    private Boolean allowedMonthly;

    private int orderValue;

    private boolean isEditable;

    private Boolean isNameUpdating;
}
