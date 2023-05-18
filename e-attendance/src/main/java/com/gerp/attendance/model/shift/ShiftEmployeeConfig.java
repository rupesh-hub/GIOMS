package com.gerp.attendance.model.shift;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "shift_employee_config",uniqueConstraints = @UniqueConstraint(name = "UNIQUE_employeeshiftconfig", columnNames = {"pis_code","shift_id"}))
public class ShiftEmployeeConfig extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_employee_config_seq_gen")
    @SequenceGenerator(name = "shift_employee_config_seq_gen", sequenceName = "seq_shift_employee_config", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;
}
