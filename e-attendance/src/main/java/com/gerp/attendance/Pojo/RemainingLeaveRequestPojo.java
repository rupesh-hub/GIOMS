package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemainingLeaveRequestPojo {

    private Long id;

    @Digits(integer=7, fraction=1)
    private Double leaveTaken;

    @Digits(integer=7, fraction=1)
    private Double leaveTakenFy;

    @Digits(integer=7, fraction=1)
    private Double accumulatedLeaveFy;

    @Digits(integer=7, fraction=1)
    private Double accumulatedLeave;

    @Digits(integer=7, fraction=1)
    private Double leaveTakenMonth;

    private Integer travelDays;

    private Long leaveSetupId;

    private Integer repetition;

    private String leaveNameEn;

    private String leaveNameNp;

    private String year;

    @Digits(integer=7, fraction=1)
    private Double remainingLeave;

    @Digits(integer=7, fraction=1)
    private Double monthlyLeaveTaken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastModifiedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private Long leavePolicyId;

    @Digits(integer=7, fraction=1)
    private Double totalAllowedLeave;

    @Digits(integer=7, fraction=1)
    private Double totalLeaveTaken;

    @Digits(integer=7, fraction=1)
    private Double leaveTakenForObsequies;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate uptoDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate adjustUpdateDate;

    @Digits(integer=7, fraction=1)
    private Double homeLeaveAdditional;
}
