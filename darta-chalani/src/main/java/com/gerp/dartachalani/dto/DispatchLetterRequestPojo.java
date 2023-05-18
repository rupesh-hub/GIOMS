package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.dartachalani.dto.document.SysDocumentsPojo;
import com.gerp.dartachalani.model.dispatch.DispatchLetterReceiverInternal;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.ReceiverType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterRequestPojo {

    private Long id;
    private LetterPrivacy letterPrivacy;
    private LetterPriority letterPriority;
    private String senderPisCode;
    private Status status;
    private String remarks;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dispatchDateEn;
    private String dispatchDateNp;
    private String subject;
    private String documentId;
    private MultipartFile document;
    private List<MultipartFile> documents;
    private List<Long> documentsToRemove;
    private List<Long> referencesToRemove;
    private Boolean isDraft;
    private String signee;
    private String content;
    private String signature;
    private Boolean signatureIsActive;
    private Boolean isContentChanged;

    private Boolean singular;
    private Boolean isEnglish;
    private Boolean isAd;
    private Boolean includeSectionId;
    private String includedSectionId;
    private Boolean isEdit;

    private ReceiverType receiverType;
    private List<DispatchLetterReceiverExternalPojo> dispatchLetterReceiverExternals;

    private DispatchLetterReceiverExternalPojo toExternalReceiver;

    private  DispatchLetterReceiverInternalPojo toReceiver;

    private List<DispatchLetterReceiverInternalPojo> toReceivers;

    private List<DispatchLetterReceiverInternalPojo> ccReceiver;

    private List<DispatchLetterReceiverExternalPojo> ccExternal;

    private List<DispatchForwardRequestPojo> dispatchForwardRequestPojos;

    private List<DispatchReceiverRequestPojo> dispatchReceiverRequestPojos;

    private List<SysDocumentsPojo> systemDocuments;

    private List<Long> referenceMemoId;
    private List<Long> receivedReferenceId;
    private List<Long> chalaniReferenceId;

    private Long taskId;
    private Long projectId;

    private String hashContent;

    private Boolean hasSubject;

    private Boolean includeSectionInLetter;

    // case when already approved letter in one office have to send to another office
    private Boolean isAlreadyApproved;

    // is draft ready to submit
    private Boolean isReadyToSubmit = Boolean.FALSE;
}
