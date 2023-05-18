package com.gerp.usermgmt.pojo.organization.orgtransfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.OrgTransferType;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.enums.TransferStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgTransferHistoryPojo {

    private Long id;

    private TransferStatus transferStatus;
    private OrgTransferType transferType;

    private Long orgTransferRequestId;

    private String fromOfficeCode;
    private String remark;

    private EmployeeMinimalPojo employee;

    private String fromDesignationCode;

    private Long fromSectionId;
    private Boolean acknowledged;

    private String targetOfficeCode;

    private String toDesignationCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate officeJoiningDateEn;

    private String approvedDateNp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvedDateEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String requestedDateNp;

    @JsonFormat(pattern = "yyyy-MM-dd")

    private LocalDate requestedDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expectedJoinDateEn;
    private String expectedJoinDateNp;


    private String approverCode;

    private String pisCode;

    private IdNamePojo targetOffice;
    private IdNamePojo fromOffice;
    private IdNamePojo fromSection;
    private EmployeeMinimalPojo approvalEmployee;



}
