package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeErrorMessagePojo {
    private String pisCode;
    private String employeeCode;
    private String designationEn;
    private String designationNp;
    private String sectionDesignationId;
    private Long contactId;
    private String positionCode;
    private String officeCode;
    private String nameNp;
    private String nameEn;
    private String errorMessage;
}
