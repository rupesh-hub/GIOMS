package com.gerp.tms.pojo.authorization;

import com.gerp.tms.model.authorization.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Temp {
    private Account account;
    private ActivityLevel activityLevel;
    private Donor Donor;
    private DonorAgent donorAgent;
    private DonorSubGroup donorSubGroup;
    private DonorGroupPojo donorGroupPojo;
    private AuthorizationActivity authorizationActivity;
    private EconomicLevel economicLevel;
    private PayingOffice payingOffice;
    private Source source;
    private SourceType sourceType;
    private SupportOffice supportOffice;
}
