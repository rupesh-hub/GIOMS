package com.gerp.usermgmt.pojo.organization.jobdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.usermgmt.config.generator.EmployeeServiceStatusConstant;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesignationDetailPojo {

    private String officeCode;
    private String organizationName;

    private String organizationNameNp;

    private LocalDate  startDate;

    private LocalDate endDate;

    private String jobStatus;

    private String jobStatusNp;

    private String newPosition;

    private String newPositionNp;

    private String newDesignation;

    private String newDesignationNp;

    private ServicePojo newService;

    private ServicePojo newGroup;

    private ServicePojo newSubGroup;




}
