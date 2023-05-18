package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyLogPojo {

    private Long id;

    private String approverNameEn;
    private String approverNameNp;
    private String requesterNameEn;
    private String requesterNameNp;

    @JsonFormat(pattern = "HH:mm:ss")
    @NotNull
    private LocalTime timeFrom;

    private Boolean isApprover;

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime timeTo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvedDate;

    private String dateNp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp createdDate;

    private String fiscalYearCode;
    private String officeCode;
    private String pisCode;
    private EmployeeDetailsPojo employee;

    private EmployeeMinimalPojo employeeDetails;

    private String location;
    private String remarks;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;
    private ApprovalDetailPojo approvalDetail;
    private ApprovalDetailMinimalPojo allApprovalList;
    private ApprovalDetailPojo delegatedApprovalDetail;

    private String approverPisCode;
    private String delegatedApproverPisCode;
    private EmployeeDetailsPojo approver;
    private String pisNameEn;
    private String pisNameNp;

    private Boolean isActive;

    public void setDelegatedApprovalDetail(ApprovalDetailPojo delegatedApprovalDetail) {
        this.delegatedApprovalDetail = delegatedApprovalDetail;
        if(!ObjectUtils.isEmpty(delegatedApprovalDetail) && delegatedApprovalDetail.getApproverPisCode() == null){
            this.delegatedApprovalDetail = null;
        }
    }

    private String dailyLogRequesterHashContent;

    private String dailyLogRequesterSignature;
    private String dailyLogContent;

    private String approvalHashContent;
    private String approvalContent;
    private String approvalSignature;
    private VerificationInformation verificationInformation;
    private Object signatureInformation;
    private String hashContent;
    private String signature;
}
