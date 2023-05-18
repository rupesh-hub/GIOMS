package com.gerp.usermgmt.pojo.organization.orgtransfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.OrgTransferType;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.usermgmt.enums.TransferStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgTransferRequestPojo {
    private Long id;
    private String employeePisCode;
    private Long fromSectionId;
    private EmployeeMinimalPojo employee;
    private IdNamePojo targetOffice;
    private String targetOfficeCode;
    private TransferStatus transferStatus;
    private String remark;
    private String requestedDateNp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDateEn;
    private String approvedDateNp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate approvedDateEn;
    private OrgTransferType transferType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expectedJoinDateEn;

    private String expectedJoinDateNp;
    private Boolean acknowledged;

    private String deviceId;

}
