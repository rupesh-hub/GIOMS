package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KaajRequestCustomPojoModified {
    private Integer id;

    private String officeCode;
//    private String fiscalYearCode;

    private String pisCode;
//    private List<String> pisCodes;
//    private List<KaajEmployeeDetailPojo> pisCodesDetail;
    private Boolean appliedForOthers;
//    private String pisNameEn;
//    private String pisNameNp;
//    private String sectionNameEn;
//    private String sectionNameNp;
//    private String designationNameNp;
//    private String designationNameEn;

    private Boolean forwardedStatus=Boolean.FALSE;

//    private List<String>kaajStatus;

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

//    private String recommendorPisCode;

    private String approverPiscode;

//    private Integer kaajTypeId;

    private String kaajTypeName;

    private String kaajTypeNameNp;

    private Boolean isActive;
    private Boolean isInternational;

//    private Long countryId;

    private String kaajNameNp;

    private IdNamePojo country;

//    private List<IdNamePojo> vehicleCategories;
    private String vehicleCategoriesNameEns;
//    private String vehicleCategoriesNameNps;

    @JsonInclude
    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
//    private DurationType durationType;
    private String location;
//    private String purpose;

    @JsonInclude
    private List<DocumentPojo> document;

    @JsonInclude
    private List<DocumentPojo> referenceDocument;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private ApprovalDetailPojo approvalDetail;
    private List<ApprovalDetailPojo> approvalDetailList;
    ApprovalDetailMinimalPojo allApprovalList;
    private ApprovalDetailPojo delegatedApprovalDetail;
    private List<EmployeeMinimalDetailsPojo> employeeMinimalDetailsPojo;

    private EmployeeMinimalDetailsPojo employeeMinimalDetail;

    private Long kaajApproveDartaNo;

    private List<KaajAppliedOthersPojo>kaajAppliedOthersPojo;

    @JsonInclude
    private String advanceAmountTravel;

    @JsonInclude
    private String remarkRegardingTravel;

//    public String getVehicleCategoriesNameEns() {
//        if(vehicleCategories!=null && !vehicleCategories.isEmpty())
//            return vehicleCategories.stream().map(x->x.getName()).collect(Collectors.joining(", "));
//        else return null;
//    }

//    public String getVehicleCategoriesNameNps() {
//        if(vehicleCategories!=null && !vehicleCategories.isEmpty())
//            return vehicleCategories.stream().map(x->x.getNameN()).collect(Collectors.joining(", "));
//        else return null;
//    }

//    public void setDelegatedApprovalDetail(ApprovalDetailPojo delegatedApprovalDetail) {
//        this.delegatedApprovalDetail = delegatedApprovalDetail;
//        if(!ObjectUtils.isEmpty(delegatedApprovalDetail) && delegatedApprovalDetail.getApproverPisCode() == null){
//            this.delegatedApprovalDetail = null;
//        }
//    }

//    private String kaajRequesterHashContent;
//
//    private String kaajRequesterSignature;
//    private String kaajRequesterContent;
//    private String approvalSignature;
//    private String approvalContent;
//    private String approvalHashContent;
//    private VerificationInformation verificationInformation;
//    private Object signatureInformation;

//    public Long getTotalDays() {
//        return ChronoUnit.DAYS.between(this.getFromDateEn(), this.getToDateEn())+1;
//    }
}
