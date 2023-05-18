package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterApprovalPojo {

    private Long approvalId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receivedDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String receivedDateNp;
    private String remarks;
    private Status approvalStatus;
    private String subject;
    private String receiverPisCode;
    private String receiverSectionCode;
    private String receiverOfficeName;
    private String receiverNameNp;
    private String receiverName;
    private String senderNameNp;
    private String senderDesignationNameNp;
    private String senderName;
    private String senderPisCode;
    private String senderSectionCode;
    private String senderDesignationCode;

    private Boolean approvalIsActive;
    private Boolean isSeen;
    private Boolean reverted;
    private List<DlApprovalDetailPojo> details;
    private Boolean signatureIsActive;
    private String signatureData;
    private VerificationInformation signatureVerification;
    private Long contentLogId;
    private Boolean isImportant;
    private Boolean isActive;
    private Integer delegatedId;
    private Boolean isDelegated = Boolean.FALSE;

    private String hash_content;

    private Boolean isReassignment = Boolean.FALSE;
    private DetailPojo reassignmentSection;

    private Boolean isTransferred = Boolean.FALSE;


    // used in signature verification log
    private String signedPis;
}
