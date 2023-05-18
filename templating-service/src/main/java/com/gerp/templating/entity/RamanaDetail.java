package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RamanaDetail {

    private String advanceAmount;

    private String date;

    private String citizenDeductionAmount;

    private String incomeTaxDeductionAmount;

    private LifeInsuranceDetail sabikLifeInsurance;

    private LifeInsuranceDetail sabikLifeInsuranceYearly;

    private  String previousRetirementPlan;

    private RetirementPlanDetail newRetirementPlan;

    private String festiveMonth;

    private String babyCareAmount;

    private String employeeName;
}
