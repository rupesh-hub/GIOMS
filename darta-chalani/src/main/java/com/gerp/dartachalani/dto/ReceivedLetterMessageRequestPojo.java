package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedLetterMessageRequestPojo {

    private Long id;
    private Long forwardId;
    private String comment;
    private String pisCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String createdDateNp;
    private String createdTimeNp;
    private Boolean isActive;

    private Integer delegatedId;
    private Boolean isDelegated = Boolean.FALSE;

    private Boolean isReassignment = Boolean.FALSE;
    private DetailPojo reassignmentSection;

    private EmployeePojo commentCreator;
}
