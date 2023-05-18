package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDataPojo {

    private Integer sn;

    private String dartaNo;

    private String RequestedDate;

    private String ApprovedDate;

    private String employeeName;

    private String date;

    private String duration;

    private String leaveName;
}
