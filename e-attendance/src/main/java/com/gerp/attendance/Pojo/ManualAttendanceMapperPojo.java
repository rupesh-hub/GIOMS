package com.gerp.attendance.Pojo;

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
@AllArgsConstructor
@NoArgsConstructor
public class ManualAttendanceMapperPojo {

    private Integer id;

    private Integer officeCode;

    private Integer recommenderPisCode;

    private Integer approverPisCode;

    private String attendanceFileUrl;

    private String attendanceFlag;

    private Boolean isApproved;

    private Boolean isRecommended;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer documentId;

    private Integer kaajRequestId;
}
