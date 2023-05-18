package com.gerp.attendance.model.shift.group;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;

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
@Table(name = "shift_employee_group")
public class ShiftEmployeeGroup extends AuditActiveAbstract {

    @Id
    @SequenceGenerator(name = "shift_employee_group_gen", sequenceName = "shift_employee_group_gen", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_employee_group_gen")
    private Long id;

    @Column(name = "name_en", columnDefinition = "VARCHAR(50)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    private String nameEn;

    @Column(name = "name_np", columnDefinition = "VARCHAR(50)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    private String nameNp;

    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String officeCode;

    private Long employeeCount;

    private Long fiscalYear;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_employee_group_id", foreignKey = @ForeignKey(name = "FK_ShiftEmployeeGroup_Mapping"))
    private Collection<ShiftEmployeeGroupMapping> shiftEmployeeGroupMappings;

    public ShiftEmployeeGroup(Long id) {
        this.id = id;
    }
}
