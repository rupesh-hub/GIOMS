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
public class KaajSummaryPojo {

    private Long kaajApprovedDartaNo;

    private String empNameEn;

    private String empNameNp;

    private String designationNameEn;

    private String designationNameNp;

    private String location;

    private String advancedAmountTravel;

    private String fromDateNp;

    private String toDateNp;

    private Long totalDays;

    private String remarksRegardingTravel;

    private String pisCode;

    private Integer eOrder;

    private Long orderNo;

    private String currentPositionAppDateBs;
}
