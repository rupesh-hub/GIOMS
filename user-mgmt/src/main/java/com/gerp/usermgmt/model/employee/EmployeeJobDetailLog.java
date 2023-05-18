package com.gerp.usermgmt.model.employee;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_job_detail_log")
public class EmployeeJobDetailLog extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "employee_job_detail_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "employee_job_detail_seq_gen", sequenceName = "employee_job_detail_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pis_code",columnDefinition = "varchar(15) references employee(pis_code)", nullable = false)
    private String pisCode;
    @Column(name = "promoted_office_code",columnDefinition = "varchar(15) references office(code)", nullable = false)
    private String promotedOfficeCode;

    @Column(name = "old_designation_code",columnDefinition = "varchar(15) references functional_designation(code)")
    private String oldDesignationCode;
    @Column(name = "new_designation_code",columnDefinition = "varchar(15) references functional_designation(code)")
    private String newDesignationCode;
    @Column(name = "new_position_code",columnDefinition = "varchar(15) references position(code)")
    private String newPositionCode;
    @Column(name = "old_position_code",columnDefinition = "varchar(15) references position(code)")
    private String oldPositionCode;
    @Column(name = "new_service_code",columnDefinition = "varchar(15) references service(code)")
    private String newServiceCode;
    @Column(name = "old_service_code",columnDefinition = "varchar(15) references service(code)")
    private String oldServiceCode;
    @Column(name = "is_period_over")
    private Boolean isPeriodOver;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "start_date_np")
    private String startDateNp;
    @Column(name = "end_date_np")
    private String endDateNp;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "is_promotion")
    private Boolean isPromotion;

}
