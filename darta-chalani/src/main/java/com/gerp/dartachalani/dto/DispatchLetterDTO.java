package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.dartachalani.dto.document.DocumentResponsePojo;
import com.gerp.dartachalani.model.dispatch.DispatchLetterReceiveInternalDetail;
import com.gerp.dartachalani.model.dispatch.DispatchPdfData;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchLetterDTO {
    private Long dispatchId;
    private Long referenceId;
    private Boolean referenceIsEditable;
    private String content;
    private String referenceCode;
    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dispatchDate;
    private String dispatchDateNp;
    private String dispatchNo;
    private String documentId;
    private String documentName;
    private Boolean isDraft;
    private LetterPriority letterPriority;
    private LetterPrivacy letterPrivacy;
    private String receiverType;
    private String senderPisCode;
    private String senderSectionCode;
    private String senderName;
    private String senderNameNp;
    private String senderOfficeCode;
    private String signee;
    private String subject;
    private String status;
    private String remarks;
    private boolean editable = false;
    private Boolean singular;
    private Boolean include;
    private Boolean isEnglish;
    private Boolean isAd;
    private Boolean includeSection;
    private String fiscalYearCode;

    private List<String> templates;
    private String ocTemplate;

    List<DispatchLetterInternalDTO> dispatchLetterInternal;
    List<DispatchLetterExternalDTO> dispatchLetterExternal;
    List<DlApprovalDetailPojo> details;

    List<DispatchLetterApprovalPojo> approval;
    private List<DocumentResponsePojo> documents;

    private List<MemoResponsePojo> memoReferences;
    private List<ReceivedLetterResponsePojo> dartaReferences;
    private List<DispatchLetterDTO> chalaniReference;

    private EmployeeMinimalPojo referenceCreator;

    private SignaturePojo signature;

    private VerificationInformation verificationInformation;

    private List<DispatchPdfData> pdfData;

    private Boolean remarksSignatureIsActive;
    private String remarksSignatureData;
    private String remarksPisCode;
    private String remarksSectionCode;
    private String remarksDesignationCode;
    private VerificationInformation remarksVerificationInformation;
    private VerificationInformation activeSignatureData;

    private Integer delegatedId;
    private EmployeeMinimalPojo remarksUserDetails;

    private Boolean isRemarksUserDelegated = Boolean.FALSE;
    //this flag is used for additional responsibility
    private Boolean isRemarksUserReassignment = Boolean.FALSE;

    private DetailPojo remarksUserReassignmentSection;
    private String remarksUserDesignationNameNp;
    private String remarksUserDesignationNameEn;

    private Boolean isTableFormat = Boolean.FALSE;
    private String hash_content;

    private Long templateHeaderId;

    private Long templateFooterId;

    private Boolean hasSubject;

    private List<DispatchLetterCommentsPojo> allComments;

    private Set<String> previousPisCodes;

    //delegated sender info
    private Boolean isDelegated = Boolean.FALSE;
    private Boolean isReassignment = Boolean.FALSE;
    private DetailPojo reassignmentSection;

    // used in signature verification log
    private String signedPis;
    private String remarksSignedPis;

    //for already approved letter
    private Boolean isAlreadyApproved = Boolean.FALSE;

    // for qr chalani
    private String  mobileNumber;
}
