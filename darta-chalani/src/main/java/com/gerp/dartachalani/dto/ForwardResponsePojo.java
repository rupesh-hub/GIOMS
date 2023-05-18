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
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  ForwardResponsePojo {

    private Long id;
    private String receiverPisCode;
    private String receiverSectionId;
    private String senderPisCode;
    private String senderSectionId;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String createdDateNp;
    private String createdTimeNp;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;
    private Boolean isActive;
    private Boolean isSuggestion;
    private Boolean isCc;
    private Boolean isSeen;
    private Boolean isReverted;
    private Integer delegatedId;
    private Boolean isImportant;
    private Boolean isDelegated=Boolean.FALSE;

    private EmployeeMinimalPojo receiver;
    private EmployeeMinimalPojo sender;
    private String senderDesignationNameNp;
    private List<ReceivedLetterMessageRequestPojo> comments;


    private Boolean isReassignment = Boolean.FALSE;
    private DetailPojo reassignmentSection;

    private Boolean isTransferred = Boolean.FALSE;
}
