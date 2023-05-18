package com.gerp.dartachalani.dto.systemFiles;

import com.gerp.dartachalani.dto.DetailPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class SystemFilesDto {

    private Long id;
    private String subject;
    private Timestamp approvedDate;
    private String approvedDateNp;
    private String approvedDateEn;
    private String pisCode;

    private Integer delegatedId;
    private EmployeeMinimalPojo creator;
    private Boolean isDelegated;
    //this flag is used for additional responsibility
    private Boolean isReassignment;

    private String letterType;
}
