package com.gerp.attendance.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

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
@Table(name = "shift_time_config")
public class ShiftTimeConfig extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_day_config_seq_gen")
    @SequenceGenerator(name = "shift_day_config_seq_gen", sequenceName = "seq_shift_day_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "checkin_time", columnDefinition = "TIME")
    private LocalTime checkinTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "checkout_time", columnDefinition = "TIME")
    private LocalTime checkoutTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "half_time", columnDefinition = "TIME")
    private LocalTime halfTime;

    private Boolean isMidNight;

    @ManyToOne
    @JoinColumn(name = "shift_day_config_id")
    private ShiftDayConfig shiftDayConfig;

}


