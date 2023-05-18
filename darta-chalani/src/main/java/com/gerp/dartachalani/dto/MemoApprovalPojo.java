package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.dartachalani.dto.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoApprovalPojo {

    private Long id;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private String approverPisCode;
    private String sectionCode;
    private String officeCode;
    private String officeNameNp;
    private EmployeeMinimalPojo approver;
    private String senderPisCode;
    private EmployeeMinimalPojo sender;
    private String senderDesignationNameNp;
    private String firstSender;

    private String remarks;
    private Boolean isActive;
    private Boolean isExternal;
    private Boolean isBack;
    private Boolean suggestion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String createdDateNp;

    private String createdTimeNp;

    private Boolean reverted;

    private Long logId;

    private Boolean isSeen;

    private Boolean isImportant = false;

    private Integer delegatedId;

    private Boolean isDelegated = Boolean.FALSE;

    //this flag is used for additional responsibility
    private Boolean isReassignment = Boolean.FALSE;

    private DetailPojo reassignmentSection;

    //this flag is for letter transfer
    private Boolean isTransferred = Boolean.FALSE;

    private String type;

    private Long memoId;
}
