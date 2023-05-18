package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAttendanceGetPojo {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp fromDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp toDateEn;

    private String fromDateNp;

    private String toDateNp;

    private String remarks;

    private ApprovalDetailPojo approvalDetail;

    private DocumentPojo document;

    private Long supportingDocumentsId;

    private String approverPisCode;

    private String fiscalYearCode;

    private String officeCode;

    private String pisCode;

    private String pisNameEn;

    private String pisNameNp;

    private String fiscalYearNp;

    private String fiscalYear;

    private String officeNameNp;

    private String officeNameEn;

    private Boolean isActive;

}
