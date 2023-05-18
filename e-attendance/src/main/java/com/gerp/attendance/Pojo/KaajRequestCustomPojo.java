package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.attendance.model.kaaj.VehicleCategory;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
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
public class KaajRequestCustomPojo {

    private Integer id;
    private String officeCode;
    private String fiscalYearCode;
    private String pisCode;
    private String employeeCode;
    private List<EmployeeDetailPojo> pisCodesDetail;
    private Boolean appliedForOthers;
    private String pisNameEn;
    private String pisNameNp;
    private String sectionNameEn;
    private String sectionNameNp;
    private String designationNameNp;
    private String designationNameEn;
    private Boolean forwardedStatus = Boolean.FALSE;
    private List<String> kaajStatus;
    private String kaajNameEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate approveDate;

    private String fromDateNp;
    private String toDateNp;
    private Boolean approverCheck;
    private Long totalDays;
    private String appliedPisCode;
    private String approverPiscode;
    private Integer kaajTypeId;
    private String kaajTypeName;
    private String kaajTypeNameNp;
    private Boolean isActive;
    private Boolean isInternational;
    private Long countryId;
    private String kaajNameNp;
    private IdNamePojo country;
    private List<IdNamePojo> vehicleCategories;

    @JsonInclude
    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType durationType;
    private String location;
    private String purpose;

    @JsonInclude
    private List<DocumentPojo> document;

    @JsonInclude
    private List<DocumentPojo> referenceDocument;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private ApprovalDetailPojo approvalDetail;
    private List<ApprovalDetailPojo> approvalDetailList;
    private ApprovalDetailMinimalPojo allApprovalList;
    private ApprovalDetailPojo delegatedApprovalDetail;
    private List<EmployeeMinimalDetailsPojo> employeeMinimalDetailsPojo;
    private EmployeeMinimalDetailsPojo employeeMinimalDetail;
    private Long kaajApproveDartaNo;
    private List<KaajAppliedOthersPojo> kaajAppliedOthersPojo;
    private List<DatesPojo> dates;

    @JsonInclude
    private String advanceAmountTravel;

    @JsonInclude
    private String remarkRegardingTravel;

    public void setDelegatedApprovalDetail(ApprovalDetailPojo delegatedApprovalDetail) {
        this.delegatedApprovalDetail = delegatedApprovalDetail;
        if (!ObjectUtils.isEmpty(delegatedApprovalDetail) && delegatedApprovalDetail.getApproverPisCode() == null) {
            this.delegatedApprovalDetail = null;
        }
    }

    private String kaajRequesterHashContent;
    private String kaajRequesterSignature;
    private String kaajRequesterContent;
    private String approvalSignature;
    private String approvalContent;
    private String approvalHashContent;
    private VerificationInformation verificationInformation;
    private Object signatureInformation;

}
