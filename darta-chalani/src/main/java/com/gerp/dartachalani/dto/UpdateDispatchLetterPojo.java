package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.Status;
import lombok.*;

import java.util.List;

@Data
public class UpdateDispatchLetterPojo {
    private Long dispatchLetterId;
    private Status status;
    private String remarks;
    private String description;
    private boolean backForReview;
    private Boolean include;
    private Boolean mySignature;
    private String pdf;

    private String signature;
    private Boolean signatureIsActive;

    private String remarksSignature;
    private Boolean remarksSignatureIsActive;

    private List<Long> dartaIds;
    private List<Long> chalaniIds;
    private List<Long> tippaniIds;
    private List<Long> documentIds;
    private Boolean includeSectionId;
    private String includedSectionId;

    private String hashContent;

}
