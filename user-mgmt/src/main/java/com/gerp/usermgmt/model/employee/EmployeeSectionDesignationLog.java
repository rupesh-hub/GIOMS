package com.gerp.usermgmt.model.employee;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_section_designation_log")
public class  EmployeeSectionDesignationLog extends AuditActiveAbstract {
    @Id
    @SequenceGenerator(name = "employee_section_designation_log_seq", sequenceName = "section_designation_seq", allocationSize = 1)
    @GeneratedValue(generator = "employee_section_designation_log_seq", strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(name = "section_designation_id", nullable = false, columnDefinition = "int references section_designation(id)")
    private Integer sectionDesignationId;


    @Column(name = "prev_employee_pis_code",columnDefinition = "varchar(15) references employee(pis_code)")
    private String prevEmployeePisCode;

    @Column(name = "new_employee_pis_code",columnDefinition = "varchar(15) references employee(pis_code)")
    private String newEmployeePisCode;

    @Column(name = "is_latest", nullable = false)
    private Boolean isLatest;
}
