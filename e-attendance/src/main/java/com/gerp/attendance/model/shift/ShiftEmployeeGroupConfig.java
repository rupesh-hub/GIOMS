package com.gerp.attendance.model.shift;

import com.gerp.attendance.model.shift.group.ShiftEmployeeGroup;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "shift_employee_group_config",uniqueConstraints = @UniqueConstraint(name = "UNIQUE_shiftemployeegroupconfig", columnNames = {"shift_employee_group_id","shift_id"}))
public class ShiftEmployeeGroupConfig extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_employee_group_config_seq")
    @SequenceGenerator(name = "shift_employee_group_config_seq", sequenceName = "shift_employee_group_config_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "shift_employee_group_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_ShiftEmployeeGroupConfig_ShiftEmployeeGroup"))
    private ShiftEmployeeGroup shiftEmployeeGroup;

}
