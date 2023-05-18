package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveRequestLatestPojo {

    private Long id;

    private Long leaveSetupId;
    private Double actualDay;

    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType leaveFor;

    private String year;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date appliedDate;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    private String leaveName;
    private String leaveNameNp;
    private String recordId;
    private String employeeCode;
    private Boolean isHoliday;
    private String description;
    private Long leavePolicyId;
    private Long periodicHolidayId;
    private String holidayName;
    private String holidayNameNp;
    private Long documentId;
    private Boolean forwardedStatus=Boolean.FALSE;
    private Boolean appliedForOthers;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate finalDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromTravelDaysEn;

    private String fromTravelDaysNp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toTravelDaysEn;

    private String toTravelDaysNp;
    private Integer travelDays;
    private ApprovalDetailPojo approvalDetail;
    private ApprovalDetailMinimalPojo allApprovalList;
    private ApprovalDetailPojo delegatedApprovalDetail;
    private String officeCode;
    private String cancelRemarks;
    private Boolean isActive;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private DocumentPojo document;
    private LeaveLogDetailPojo previousLeaveDetail;
    private String pisCode;
    private String employeeNameEn;
    private String employeeNameNp;
    private String fdNameEn;
    private String fdNameNp;
    private String sectionNameEn;
    private String sectionNameNp;
    private String officeNameEn;
    private String officeNameNp;
    private List<OfficePojo> higherOffices;
    private Double totalDays;
    private Double actualLeaveDays;
    private Integer fiscalYear;
    private List<String> leaveStatus;
    private String leaveRequestHashContent;
    private String leaveRequestSignature;
    private String leaveRequestContent;
    private String approvalContent;
    private String approvalSignature;
    private String approvalHashContent;
    private VerificationInformation verificationInformation;
    private Object signatureInformation;

    /**
     *
     * @param requestedEmployeeServiceCode
     */
    private String requestedEmployeeServiceCode;

    public void setDelegatedApprovalDetail(ApprovalDetailPojo delegatedApprovalDetail) {
        this.delegatedApprovalDetail = delegatedApprovalDetail;
        if(!ObjectUtils.isEmpty(delegatedApprovalDetail) && delegatedApprovalDetail.getApproverPisCode() == null){
            this.delegatedApprovalDetail = null;
        }
    }
}
