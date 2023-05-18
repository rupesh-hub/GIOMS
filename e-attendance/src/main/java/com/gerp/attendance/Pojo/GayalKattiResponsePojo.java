package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.attendance.Pojo.document.DocumentMasterPojo;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GayalKattiResponsePojo {

    private Long id;
    private String pisCode;
    private String officeCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp createdDate;

    private String fromDateNp;
    private String toDateNp;
    private String remarks;
    private String documentMasterId;
    private Boolean isActive;

//    private DocumentPojo document;
    private List<DocumentMasterPojo> documents;


    private EmployeeMinimalPojo employeeDetails;
}
