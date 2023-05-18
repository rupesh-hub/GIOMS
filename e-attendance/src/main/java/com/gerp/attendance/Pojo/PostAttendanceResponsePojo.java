package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAttendanceResponsePojo {

    private Long id;
    private Long detailId;
    private Long approvalId;

    private String officeCode;
    private String pisCode;
    private String fiscalYearCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String detailRemarks;
    private String supportingDocumentId;
    private String fromDateNp;
    private String toDateNp;

    private Status status;
    private String approverPisCode;
    private String approvalRemarks;

}
