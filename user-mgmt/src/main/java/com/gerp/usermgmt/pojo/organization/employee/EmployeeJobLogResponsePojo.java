package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeJobLogResponsePojo {
    private Long id;
    private String employeeNameEn;
    private String employeeNameNp;
    private String pisCode;
    private FunctionalDesignationPojo oldFunctionalDesignation;
    private FunctionalDesignationPojo newFunctionalDesignation;
    private IdNamePojo createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String startDateNp;
    private String endDateNp;
    private Boolean isActive;
    private Boolean isPeriodOver;
    private Boolean isPromotion;
}
