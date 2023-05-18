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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveRequestMainPojo {

    private Long detailId;
    private String leavePisCode;
    private String employeeCode;
    private Long leaveSetupId;
    private Double actualDay;
    private Long leavePolicyId;
    private String appliedPisCode;
    private Boolean appliedForOthers;
    private EmployeeDetailPojo appliedForOtherPojo;
    private String approverPisCode;
    private Long dartaNumber;
    private Double actualDays;

    private String year;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date appliedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date approvedDate;

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
    private Boolean isHoliday;
    private String description;
    private Long periodicHolidayId;
    private String holidayName;
    private String holidayNameNp;
    private Long documentId;
    private Boolean forwardedStatus = Boolean.FALSE;
    private String employeeNameEn;
    private String employeeNameNp;
    private String fdNameEn;
    private String fdNameNp;
    private String sectionNameEn;
    private String sectionNameNp;
    private String officeNameEn;
    private String officeNameNp;

    private String requesterNameEn;
    private String requesterNameNp;

    private String approverNameEn;
    private String approverNameNp;

    private List<DocumentPojo> document;
    private String leaveApproveDartaNo;

    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType leaveFor;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private ApprovalDetailPojo approvalDetail;
    private ApprovalDetailMinimalPojo allApprovalList;
    private EmployeeMinimalDetailsPojo employeeMinimalDetail;
    private List<OfficePojo> higherOffices;
    private Double totalDays;
    private Double actualLeaveDays;
    private Integer fiscalYear;
    private List<String> leaveStatus;

    /*SIGNATURE INFO*/
    private String leaveRequestHashContent;
    private String leaveRequestSignature;
    private String leaveRequestContent;
    private String approvalContent;
    private String approvalSignature;
    private String approvalHashContent;
    private VerificationInformation verificationInformation;
    private Object signatureInformation;

}
