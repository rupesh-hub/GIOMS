package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAttendanceRequestPojo {

    private Long id;
    private String officeCode;
    private String pisCode;
    private String approverPisCode;
    private String fiscalYearCode;
    private List<PostAttendanceDetailPojo> requestDetail;

}
