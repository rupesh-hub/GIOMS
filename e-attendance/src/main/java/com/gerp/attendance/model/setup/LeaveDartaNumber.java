package com.gerp.attendance.model.setup;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.generic.api.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "leave_darta_number", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_name_leave_darta_number", columnNames = {"office_code", "fiscal_year_code","registration_no"}))
public class LeaveDartaNumber extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_darta_number_seq_gen")
    @SequenceGenerator(name = "leave_darta_number_seq_gen", sequenceName = "seq_leave_darta_number", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(10)")
    private String officeCode;

    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    @Column(name = "registration_no")
    private Long registrationNo;

}
