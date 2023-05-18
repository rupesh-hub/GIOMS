package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemainingLeaveForLeaveRequestPojo {
    private String pisCode;

    private String leaveNameEn;

    private String leaveNameNp;

    private Double remainingLeave;

    private Double leaveTaken;

    private Double monthlyLeaveTaken;

    private Integer travelDays;

    private Double accumulatedLeave;

    private Double accumulatedLeaveFy;

    private Double totalLeave;

    private Integer totalRepetition;

    private Integer repetition;

    private Integer maximumLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate uptoDate;

    private Double homeLeaveAdditional;

    private String year;



}
