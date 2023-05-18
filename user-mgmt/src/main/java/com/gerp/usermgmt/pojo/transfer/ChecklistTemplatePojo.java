package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistTemplatePojo {
        public boolean durationComplete;
        public boolean durationInComplete;
        public boolean workFirstTime;
        public boolean workRepetition;
        public boolean nearHome;
        public boolean farHome;
        public boolean coupleInPublicService;
        public boolean coupleNotInPublicService;
        public boolean employeeAboveFifty;
        public boolean employeeBelowFifty;
        public boolean hasFazil;
        public boolean hasNotfazil;
        public boolean hasStudyLetter;
        public boolean hasNotStudyLetter;
        public boolean hasMinistryLetter;
        public boolean hasNotMinistryLetter;
        public boolean hasPostVacant;
        public boolean hasNotPostVacant;
        public boolean experienceAboveTenYears;
        public boolean experienceBelowTenYears;
        public boolean holidayForOneYear;
        public boolean holidayBelowOneYear;
        public String attendanceDays;
        public String employeeDesignation;
        public EmployeeOtherDetailsPojo employeeDetail;
}
