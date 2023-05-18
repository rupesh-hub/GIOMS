package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.dartachalani.dto.document.DocumentPojo;
import com.gerp.dartachalani.dto.json.StatusKeyValueOptionSerializer;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedLetterResponsePojo {

    private Long id;
    private Long referenceId;
    private Boolean referenceIsEditable;
    private LetterPrivacy letterPrivacy;
    private LetterPriority letterPriority;
    private String pisCode;
    private EmployeeMinimalPojo employee;
    private String officeCode;
    private String fiscalYearCode;
    private String senderOfficeCode;
    private String senderOfficeName;
    private String senderOfficeNameNp;
    private OfficePojo officeDetails;
    private String registrationNo;
    private String referenceNo;
    private String dispatchNo;
    private Long dispatchId;
    private Boolean include;
    private Boolean isEnglish;
    private Boolean isAd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String createdDateNp;
    private String createdTimeNp;
    private String createdDateBs;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dispatchDateEn;
    private String dispatchDateNp;
    private String dispatchDateBs;
    private String subject;
    private Long documentMasterId;
    private Boolean entryType;
    private Boolean isDraft;
    private Boolean isActive;
    private String content;
    private String letterType;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private String template;

    private Long prevId;
    private Long nextId;

    private String signature;
    private Boolean signatureIsActive;
    private VerificationInformation verificationInformation;

    private List<DocumentPojo> document;
    private ReceivedLetterDetailResponsePojo details;
    private List<ForwardResponsePojo> forwards;

    private List<MemoResponsePojo> memoReferences;
    private List<ReceivedLetterResponsePojo> dartaReferences;
    private List<DispatchLetterDTO> chalaniReferences;

    private EmployeeMinimalPojo referenceCreator;

    private ForwardResponsePojo activeDarta;

    private List<String> pdfData;

    private String remarks;
    private Boolean isSingular;
    private Boolean isReceiver;
    private Boolean remarksSignatureIsActive;
    private String remarksSignature;
    private String remarksPisCode;
    private VerificationInformation activeSignatureData;
    private String salutation;
    private Boolean manualIsCc;
    private DesignationPojo signatureDesignationData;
    private Boolean isImportant = false;
    private Boolean isImportantHead = false;
    private Boolean currentIsActive;
    private Integer delegatedId;
    private Boolean isCC = Boolean.FALSE;
    private Boolean isTableFormat = Boolean.FALSE;

    private String hash_content;
    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status completionStatus;

    private Set<String> previousPisCodes;

    private Boolean isReassignment = Boolean.FALSE;
    private DetailPojo reassignmentSection;
}
