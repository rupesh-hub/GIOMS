package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.dartachalani.dto.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.ReceiverType;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterResponsePojo {
    private Long id;
    private String dispatchNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dispatchDateEn;
    private String dispatchDateNp;
    private String dispatchDateBs;
    private ReceiverType receiverType;
    private String subject;
    private String receiverName;
    private boolean withinOrganization;
    private boolean isActive;
    private boolean isDraft;
    private String content;
    private LetterPriority letterPriority;
    private LetterPrivacy letterPrivacy;
    private String senderPisCode;
    private String signee;
    private String alias;
    private String ccReceiverName;
    private String toReceiverName;
    private String externalReceiverName;
    private Long documentId;
    private String documentName;
    private String status;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status dlStatus;
    private String remarks;
    private boolean isEditable;
    private EmployeePojo creator;
    private String referenceCode;
    private Boolean isImportant = false;
    private Boolean approvalIsActive;
    private Status approvalStatus;
    private Integer delegatedId;

    List<DispatchLetterInternalDTO> dispatchLetterInternal;
    List<DispatchLetterExternalDTO> dispatchLetterExternal;

    List<DispatchLetterApprovalPojo> approval;

    private String senderOfficeCode;
    private OfficePojo senderOfficeDetail;
    private EmployeePojo senderDetail;

    private Set<String> previousPisCodes;

    private Integer draftShareCount = 0;
}
