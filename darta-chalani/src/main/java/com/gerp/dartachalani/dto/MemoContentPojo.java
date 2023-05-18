package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.dartachalani.dto.document.SysDocumentsPojo;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoContentPojo {

    private Long id;
    private Long memoId;
    private String content;
    private String pisCode;
    private String officeCode;
    private String sectionCode;
    private String designationCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp createdDate;

    private String createdDateNp;
    private Boolean isActive;

    private EmployeeMinimalPojo employee;
    private SectionPojo employeeSection;
    private String employeeDesignationNameNp;
    private String employeeDesignationNameEn;
    private String officeName;
    private String officeNameNp;
    private Boolean editable;
    private Boolean isExternal;
    private Boolean isExternalEditable;
    private String signature;
    private Boolean signatureIsActive;
    private Integer delegatedId;
    private Boolean isDelegated = Boolean.FALSE;

    private VerificationInformation verificationInformation;

    private List<MultipartFile> documents;
    private List<Long> documentsToRemove;

    private List<SysDocumentsPojo> systemDocuments;

    private String hashContent;

    private Boolean isReassignment = Boolean.FALSE;

    private DetailPojo reassignmentSection;
}
