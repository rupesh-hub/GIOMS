package com.gerp.attendance.Pojo;

import com.poiji.annotation.ExcelCellName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RemainingLeaveTransformed {

    @ExcelCellName("pisCode")
    private String pisCode;

    @ExcelCellName("LeaveName")
    private String LeaveName;

    @ExcelCellName("LeavePolicyID")
    private Long  LeavePolicyID;

    @ExcelCellName("LeaveTakenFY")
    private Double LeaveTakenFY;

    @ExcelCellName("LeaveTaken")
    private Double LeaveTaken;

    @ExcelCellName("remainingLeave")
    private Double remainingLeave;

    @ExcelCellName("accumulatedLeave")
    private Double accumulatedLeave;

    @ExcelCellName("accumulatedLeaveFY")
    private Double accumulatedLeaveFY;

    @ExcelCellName("monthlyLeaveTaken")
    private Double monthlyLeaveTaken;

    @ExcelCellName("travelDays")
    private Integer travelDays;

    @ExcelCellName("repetition")
    private Integer repetition;

    @ExcelCellName("fiscalYear")
    private Integer fiscalYear;

}
