package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.manualattendance.ManualAttendanceBulkPojo;
import com.gerp.attendance.Pojo.manualattendance.ManualExcelDataPojo;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManualAttendancePojo {

    private Long id;
    private String approverPiscode;
    private Boolean isApprover;
    private Boolean appliedForOthers;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private String pisCode;
    private String officeCode;
    private Long fiscalYearCode;
    private String remarks;
    private Boolean isActive;
    private ApprovalDetailPojo approvalDetail;
    private ApprovalDetailPojo delegatedApprovalDetail;
    private EmployeeDetailPojo requesterInfo;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;


    private List<MultipartFile> document;
    private List<DocumentPojo> documents;
    private String recordId;
    private FileUploadPojo fileUploadPojo;
    private List<FileUploadPojo> fileUploadPojos;
    private List<ManualExcelDataPojo> excelDataPojoList;
    private EmployeeMinimalPojo employeeDetails;
    private String manualAttendanceRequesterHashContent;
    private String content;
    private String manualAttendanceRequesterSignature;
    private VerificationInformation verificationInformation;
    private Object signatureInformation;
    private List<ManualAttendanceBulkPojo> manualAttendanceBulkPojo;

    public void setDelegatedApprovalDetail(ApprovalDetailPojo delegatedApprovalDetail) {
        this.delegatedApprovalDetail = delegatedApprovalDetail;
        if (!ObjectUtils.isEmpty(delegatedApprovalDetail) && delegatedApprovalDetail.getApproverPisCode() == null) {
            this.delegatedApprovalDetail = null;
        }
    }

}
