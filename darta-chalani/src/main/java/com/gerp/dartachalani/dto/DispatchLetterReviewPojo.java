package com.gerp.dartachalani.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DispatchLetterReviewPojo {
    private Long id;
    private String receiverOfficeCode;

    private String receiverPisCode;

    private String receiverSectionCode;

    private LocalDate receivedDate;

    private String receivedDateNp;
    private String subject;
    private String status;
    private String remarks;

    private Long dispatchLetterId;

    private Boolean mySignature;

    private String signature;
    private Boolean signatureIsActive;

    private String remarksSignature;
    private Boolean remarksSignatureIsActive;

    private Boolean includeSectionId;
    private String includedSectionId;

    private String hashContent;
}
