package com.gerp.kasamu.pojo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class KasamuMasterRequestPojo {

    private Long id;

    @NotNull
    private String valuationPeriod;

//    private String officeCode;

    @NotNull
    @NotEmpty
    private String employeePisCode;


    @NotNull
    @NotEmpty
    private String supervisorPisCode;


//    private String evaluatorPisCode;

    @NotNull
    private Long regdNum;


//    @NotNull
//    private String currentOfficeCode;


    @NotNull
    private Boolean submittedStatus;

    private String valRemarks;


//    private String empShreni;

    @NotNull
    private Set<Long> kasamuDetailIds;

    @NotEmpty
    @NotNull
    private String fiscalYear;

    private Boolean isForNonGazetted;

//    private String progressAchievedResult;
//
//
//    private String deadlineAchievedResult;

    private Set<String> offices;
}
