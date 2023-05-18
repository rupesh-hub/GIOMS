package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import java.time.LocalDate;
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
public class RemainingLeavePojo {


    private String pisCode;
    private List<RemainingLeaveRequestPojo> leaveDetails;
    private RemainingLeaveRequestPojo leaveDetail;

    private Boolean fromPreviousSystem=Boolean.FALSE;

    /*UPDATE REMAINING LEAVE FIELDS*/
    private Long leavePolicyId;
    private Long leaveSetupId;
    private String year;
    private Long id;
    private Integer travelDays;
    @Digits(integer=7, fraction=1)
    private Double accumulatedLeaveFy;
    @Digits(integer=7, fraction=1)
    private Double accumulatedLeave;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate uptoDate;
    @Digits(integer=7, fraction=1)
    private Double leaveTakenMonth;
    @Digits(integer=7, fraction=1)
    private Double homeLeaveAdditional;
    @Digits(integer=7, fraction=1)
    private Double leaveTakenForObsequies;

}
