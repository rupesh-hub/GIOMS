package com.gerp.usermgmt.model.employee;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_joining_date")
public class EmployeeJoiningDate extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "employee_joining_date_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "employee_joining_date_seq_gen", sequenceName = "employee_joining_date_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "join_date_en")
    private LocalDate joinDateEn;

    @Column(name = "join_date_np",columnDefinition = "VARCHAR(20)")
    private String joinDateNp;

    @Column(name = "end_date_en")
    private LocalDate endDateEn;

    @Column(name = "end_date_np",columnDefinition = "VARCHAR(20)")
    private String endDateNp;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;
}
