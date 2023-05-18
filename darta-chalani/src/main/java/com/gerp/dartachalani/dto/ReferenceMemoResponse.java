package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.dartachalani.dto.document.DocumentPojo;
import com.gerp.dartachalani.dto.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ReferenceMemoResponse {

    private String sn;
    private Long id;
    private String subject;
    private String referenceNo;
    private String aPisCode;
    private EmployeeMinimalPojo referenceEmployee;
    private Boolean isActive;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp approvedDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String approvedDateBs;
    private String approvedDateNp;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private List<DocumentPojo> document;
}
