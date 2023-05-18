package com.gerp.dartachalani.dto.kasamu;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.dartachalani.dto.EmployeePojo;
import com.gerp.dartachalani.dto.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.Status;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class KasamuStateResponsePojo {
    private Long id;
    private String receiverPisCode;
    private String receiverSectionCode;
    private String senderPisCode;
    private String senderSectionCode;
    private String description;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private EmployeePojo sender;
    private EmployeePojo receiver;

    private Boolean isCc;
    private Boolean isSeen;
    private Boolean isReverted;
    private Integer delegatedId;
    private Boolean isImportant;
    private Boolean isDelegated=Boolean.FALSE;

    private String senderDesignationNameNp;
    private String createdDateNp;
    private String createdDateBs;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp createdDate;

    private List<KasamuCommentPojo> comments;
}
