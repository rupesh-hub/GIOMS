package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDashboardPojo{

    private String officeCode;
    private Long leaveCount;
    private Long kaajCount;
    private String orderStatus;

//    private String officeCode;
//    private String officeNameEn;
//    private String officeNameNp;
//    private Long leaveCount;
//    private Long kaajCount;
//    private Long manualCount;
//    private Long autoCount;
//    private Long totalDarta;
//    private Long chalaniCount;
//    private Long tippaniCount;
//    private String orderStatus;

}
