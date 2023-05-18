package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class RawanaDetailsPojo {
    private Long id;
    private Long transferHistoryId;
    private Boolean handover;
    private LocalDate transferDateEn;
    private String transferDateNp;
    private Integer totalAttendance;
    private Double basicSalary;
    private Double increasedSalary;
    private LocalDate lastSalaryReceivedDateEn;
    private String lastReceivedDateNp;
    private Double providentFund;
    private Double employeeProvidentFund;
    private Double paymentMedical;
    private LocalDate datePaymentReceiptEn;
    private String datePaymentReceiptNp;
    private Double dailyExpenses;
    private LocalDate salaryIncrementDateEn;
    private String salaryIncrementDateNp;
    private String cit;
    private Double incomeTax;
    private LocalDate insuranceRegisteredEn;
    private String insuranceRegisteredNp;
    private LocalDate insurancePaidEn;
    private String insurancePaidNp;
    private String oldPension;
    private LocalDate newPensionEn;
    private String newPensionNp;
    private LocalDate festivalDateEn;
    private String festivalDateNp;
    private String festivalName;
    private String festivalExpense;
    private String paternityCare;
    private Set<String> sendToOffices;
}
