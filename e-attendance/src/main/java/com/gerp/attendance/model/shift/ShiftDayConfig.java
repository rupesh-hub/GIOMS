package com.gerp.attendance.model.shift;

import com.gerp.shared.enums.Day;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

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
@Table(name = "shift_day_config")
public class ShiftDayConfig extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_day_config_seq_gen")
    @SequenceGenerator(name = "shift_day_config_seq_gen", sequenceName = "seq_shift_day_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Boolean isWeekend;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_day_config_id", foreignKey = @ForeignKey(name = "FK_ShiftTimeConfig_ShiftDayConfig"))
    private Collection<ShiftTimeConfig> shiftTimeConfigs;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Day day;

    private Day dayOrder;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;
}


