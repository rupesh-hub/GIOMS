package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.usermgmt.enums.DesignationType;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FunctionalDesignationPojo {
    private String code;

    @NotNull
    private String nameEn;
    @NotNull
    private String nameNp;
    private String salutation;
    private Long orderNo;
    private String employeeServiceStatusCode;
    private DesignationType designationType;
    private Boolean manuallyCreated;
    private Long organisationTypeId;
    private Long organisationType;


    @JsonIgnore
    public FunctionalDesignation getEntity() {
        FunctionalDesignation functionalDesignation = new FunctionalDesignation();
        functionalDesignation.setCode(this.code);
        return functionalDesignation;
    }
}
