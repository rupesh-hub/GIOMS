package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeePromotionPojo {

    @NotNull
    private String pisCode;
    private String employeeCode;
    private ServicePojo service;
    private FunctionalDesignationPojo functionalDesignation;
    private PositionPojo position;
    private Boolean isPromotion;

}
