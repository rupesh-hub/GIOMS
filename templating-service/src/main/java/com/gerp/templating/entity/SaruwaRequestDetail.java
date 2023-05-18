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
public class SaruwaRequestDetail {
    private Boolean durationComplete;

    private Boolean durationInComplete;

    private Boolean workFirstTime;

    private Boolean workRepetition;

    private Boolean nearHome;

    private Boolean farHome;

    private Boolean coupleInPublicService;

    private Boolean coupleNotInPublicService;

    private Boolean employeeAboveFifty;

    private Boolean employeeBelowFifty;

    private Boolean hasFazil;

    private Boolean hasNotfazil;

    private Boolean hasStudyLetter;

    private Boolean hasNotStudyLetter;

    private Boolean hasMinistryLetter;

    private Boolean hasNotMinistryLetter;

    private Boolean hasPostVacant;

    private Boolean hasNotPostVacant;

    private Boolean experienceAboveTenYears;

    private Boolean experienceBelowTenYears;

    private Boolean holidayForOneYear;

    private Boolean holidayBelowOneYear;

    private String attendanceDays;

    private EmployeeDetail employeeDetail;

    private String employeeDesignation;

}
