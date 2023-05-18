package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.dartachalani.dto.document.DocumentPojo;
import com.gerp.dartachalani.dto.json.StatusKeyValueOptionSerializer;
import com.gerp.dartachalani.dto.template.TippaniDetail;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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
public class MemoResponsePojo {

    private Long id;
    private Long referenceId;
    private String subject;
    private String content;

    private String pisCode;
    private String officeCode;
    private String sectionCode;
    private String designationCode;
    private String officeName;
    private String officeNameNp;
    private EmployeeMinimalPojo employee;
    private SectionPojo employeeSection;
    private String employeeDesignationNameNp;
    private String employeeDesignationNameEn;
    private String referenceNo;
    private EmployeePojo creator;
    private DetailPojo creatorSection;

    private String aPisCode;
    private Boolean referenceIsEditable;
    private EmployeeMinimalPojo referenceEmployee;

    private Long documentMasterId;
    private Boolean isDraft;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    private String createdDateNp;

    private String createdTimeNp;
    private String createdDateBs;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private List<DocumentPojo> document;
    private List<ForwardResponsePojo> forwards;
    private List<MemoContentPojo> contents;

    private List<MemoApprovalPojo> approval;
    private List<MemoApprovalPojo> suggestion;

    private List<ReferenceMemoResponse> referencedMemos;

    private List<DispatchLetterDTO> referenceDispatch;

    private List<ReceivedLetterResponsePojo> referencedReceived;

    private Boolean forwarded = true;

    private Boolean isSuggestion = true;

    private Boolean contentIsPresent = true;

    private String template;

    private String signature;

    private String pdf;

    private Long tippaniNo;

    private Boolean signatureIsActive;

    private List<String> referenceTemplates;

    private TippaniDetail templateContent;

    private VerificationInformation verificationInformation;

    private Boolean isImportant;

    private Boolean approvalIsActive;
    private Boolean suggestionIsActive;
    private Status approvalStatus;
    private Status suggestionStatus;

    private Integer delegatedId;

    private OfficePojo creatorOfficeDetails;

    private String hash_content;

    private Set<String> previousPisCodes;

    private Boolean isDelegated = Boolean.FALSE;
    private Boolean isReassignment = Boolean.FALSE;
    private DetailPojo reassignmentSection;

    private Long templateHeaderId;
    private Long templateFooterId;

    private String tippaniHeader;

    //this value gives the id of memo from which this memo is referenced to differentiate normal tippani creation view and reference view
    private Long memoReferencedFrom;

    private Integer draftShareCount = 0;
}
