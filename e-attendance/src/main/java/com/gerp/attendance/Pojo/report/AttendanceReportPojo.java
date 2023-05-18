package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceReportPojo {

    private String nameEn;
    private String nameNp;
    private String fdNameEn;
    private String fdNameNp;
    private String oldDesignationEn;
    private String oldDesignationNp;
    private String dateEn;
    private String dateNp;
    private String pisCode;
    private String originalPisCode;
    private Boolean isLeft = Boolean.FALSE;
    private Boolean isJoin = Boolean.FALSE;
    private Map<Integer, List<AttendanceStatusPojo>> monthlyAttendance;
    private List<AttendanceStatusPojo> report;

}
