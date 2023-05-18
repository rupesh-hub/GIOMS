package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RamanaDetailLetterPojo {
    public String advanceAmount;
    public String date;
    public String citizenDeductionAmount;
    public String incomeTaxDeductionAmount;
    public SabikLifeInsurance sabikLifeInsurance;
    public SabikLifeInsurance sabikLifeInsuranceYearly;
    public String previousRetirementPlan;
    public NewRetirementPlan newRetirementPlan;
    public String festiveMonth;
    public String babyCareAmount;
    public String employeeName;
}
