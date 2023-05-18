package com.gerp.attendance.model.shift.group;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "shift_employee_group_mapping")
public class ShiftEmployeeGroupMapping extends AuditActiveAbstract {

    @Id
    @SequenceGenerator(name = "shift_employee_group_mapping_seq", sequenceName = "shift_employee_group_mapping_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_employee_group_mapping_seq")
    private Long id;

    @Column(name = "pis_code")
    @Size(max = 20)
    private String pisCode;

    @ManyToOne
    @JoinColumn(name = "shift_employee_group_id")
    private ShiftEmployeeGroup group;
}
