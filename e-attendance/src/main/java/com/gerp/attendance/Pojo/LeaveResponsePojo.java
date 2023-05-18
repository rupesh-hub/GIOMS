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
public class LeaveResponsePojo {

    private Long id;
    private String requesterFullNameEn;
    private String requesterFullNameNp;
    private String requesterDesignationEn;
    private String requesterDesignationNp;
    private String requesterOfficeNameEn;
    private String requesterOfficeNameNp;
    private String requesterPisCode;
    private String requestedEmployeeServiceCode;
    private String employeeCode;

    private String forwardedDate;
    private String officeCode;
    private boolean approver;
    private Double travelDays;

    private String leaveEmpPisCode;
    private String leaveEmpFullNameEn;
    private String leaveEmpFullNameNp;
    private String leaveEmpDesignationEn;
    private String leaveEmpDesignationNp;
    private String leaveEmpOfficeNameEn;
    private String leaveEmpOfficeNameNp;

    private String appFullNameEn;
    private String appFullNameNp;
    private String appDesignationEn;
    private String appDesignationNp;
    private String appOfficeNameEn;
    private String appOfficeNameNp;
    private String appPisCode;

    private String reviewerFullNameEn;
    private String reviewerFullNameNp;
    private String reviewerDesignationEn;
    private String reviewerDesignationNp;
    private String reviewerOfficeNameEn;
    private String reviewerOfficeNameNp;
    private Boolean forwardedStatus;

    private boolean deligated;
    private boolean reassignment;
    private boolean delIsOfficeHead;

    private String leaveNameEn;
    private String leaveNameNp;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromTravelDays;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toTravelDays;

    private String remarks;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date appliedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date approvedDate;

    private Boolean appliedForOthers;

    private Long leavePolicyId;
    private Long leaveSetupId;
    private String year;

    private Double actualDays;

    private String holidayName;
    private Boolean isHoliday;

    private Double totalLeave;
    private Double remainingLeave;
    private Double accumulatedLeave;
    private Double accumulatedLeaveFy;


    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType leaveFor;

    private List<DocumentPojo> document;

    private List<OfficePojo> higherOffices;

    /*TODO: SIGNATURE INFO*/
    private String leaveRequestHashContent;
    private String leaveRequestSignature;
    private String leaveRequestContent;

    private String approvalContent;
    private String approvalSignature;
    private String approvalHashContent;

    private VerificationInformation verificationInformation;
    private Object signatureInformation;

    private String documentName;
}
