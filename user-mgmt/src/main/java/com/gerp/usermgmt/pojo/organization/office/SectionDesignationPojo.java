package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.usermgmt.pojo.SectionDesignationLogResponsePojo;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePojo;
import com.gerp.usermgmt.pojo.organization.employee.PositionPojo;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionDesignationPojo {

    private Integer id;

    private EmployeePojo employee;
    private EmployeePojo createdBy;
    private EmployeePojo lastModifiedBy;
    private String pisCode;
    private Boolean isActive;
    private Boolean isOnTransferProcess;

    private IdNamePojo functionalDesignation;
    private PositionPojo position;

    private String functionalDesignationCode;

    private Long sectionId;

    private SectionPojo section;
    private ServicePojo service;
    private ServicePojo group;
    private ServicePojo subGroup;

    private String positionCode;
    private String serviceCode;
    private String groupCode;
    private String subGroupCode;

    private Integer noOfVacancy;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd ")
    private LocalDate lastModifiedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private List<SectionDesignationLogResponsePojo> employeeAssignLog;
}
