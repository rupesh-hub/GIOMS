package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "rawana_details")
public class RawanaDetails extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rawana_details_seq_gen")
    @SequenceGenerator(name = "rawana_details_seq_gen", sequenceName = "rawana_details_config", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "transfer_history_id")
    private Long transferHistoryId;

    @Column(name = "handover")
    private Boolean handover;
    @Column(name = "tranfer_date_en")
    private LocalDate transferDateEn;
    @Column(name = "tranfer_date_np", columnDefinition = "VARCHAR(10)")
    private String transferDateNp;
    @Column(name = "total_attendance")
    private Integer totalAttendance;
    @Column(name = "basic_salary")
    private Double basicSalary;
    @Column(name = "increased_salary")
    private Double increasedSalary;
    @Column(name = "last_salary_received_en")
    private LocalDate lastSalaryReceivedDateEn;
    @Column(name = "last_salary_received_np",columnDefinition = "VARCHAR(10)")
    private String lastReceivedDateNp;
    @Column(name = "provident_fund")
    private Double providentFund;
    @Column(name = "employee_provident_fund")
    private Double employeeProvidentFund;
    @Column(name = "payment_medical")
    private Double paymentMedical;
    @Column(name = "date_payment_receipt_en")
    private LocalDate datePaymentReceiptEn;
    @Column(name = "date_payment_receipt_np")
    private String datePaymentReceiptNp;
    @Column(name = "daily_expenses")
    private Double dailyExpenses;
    @Column(name = "salary_increment_date_en")
    private LocalDate salaryIncrementDateEn;
    @Column(name = "salary_increment_date_np",columnDefinition = "VARCHAR(10)")
    private String salaryIncrementDateNp;
    @Column(name = "cit",columnDefinition = "VARCHAR(20)")
    private String cit;
    @Column(name = "income_tax")
    private Double incomeTax;
    @Column(name = "insurance_registered_en")
    private LocalDate insuranceRegisteredEn;
    @Column(name = "insurance_registered_np",columnDefinition = "VARCHAR(10)")
    private String insuranceRegisteredNp;
    @Column(name = "insurance_paid_en")
    private LocalDate insurancePaidEn;
    @Column(name = "insurance_paid_np",columnDefinition = "VARCHAR(10)")
    private String insurancePaidNp;
    @Column(name = "old_pension",columnDefinition = "VARCHAR(50)")
    private String oldPension;
    @Column(name = "new_pension_en")
    private LocalDate newPensionEn;
    @Column(name = "new_pension_np",columnDefinition = "VARCHAR(10)")
    private String newPensionNp;
    @Column(name = "festival_date_en")
    private LocalDate festivalDateEn;
    @Column(name = "festival_date_np",columnDefinition = "VARCHAR(10)")
    private String festivalDateNp;
    @Column(name = "festival_name",columnDefinition = "VARCHAR(50)")
    private String festivalName;
    @Column(name = "festival_expense",columnDefinition = "VARCHAR(100)")
    private String festivalExpense;
    @Column(name = "paternity_care",columnDefinition = "TEXT")
    private String paternityCare;
    @Column(name = "approval_status",columnDefinition = "VARCHAR(10)")
    private String approvalStatus;
    @Column(name = "approved_by", columnDefinition = "VARCHAR(10)")
    private String approvedBy;
}
