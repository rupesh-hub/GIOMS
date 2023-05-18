package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class DraftShareLogPojo {

    private Status status;
    private String pisCode;
    private EmployeeMinimalPojo employee;
    private Timestamp createdDate;
    private String createdDateBs;
    private String createdDateNp;
    private Boolean isModified = Boolean.TRUE;
}
