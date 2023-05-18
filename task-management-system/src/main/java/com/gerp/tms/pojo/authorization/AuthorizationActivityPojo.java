package com.gerp.tms.pojo.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationActivityPojo {
    private Integer id;
    private String targetedOutcomes;
    private String fiscalYearId;
    private Double amount;
    private List<SupportOfficePojo> supportOfficeList;
    private EconomicLevelPojo economicLevel;
    private ActivityLevelPojo activityLevel;
    private PayingOfficePojo payingOffice;
    private AccountPojo account;
    private SourceTypePojo sourceType;
    private DonorPojo donor;
}
