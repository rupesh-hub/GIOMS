package com.gerp.attendance.model.shift;

import com.gerp.shared.enums.EnglishDayType;
import com.gerp.shared.enums.NepaliDay;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.FieldErrorConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "day", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_day", columnNames = "name_np"))
public class Day extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "day_seq_gen")
    @SequenceGenerator(name = "day_seq_gen", sequenceName = "seq_day", initialValue = 1, allocationSize = 1)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name_np", columnDefinition = "VARCHAR(50)")
    private NepaliDay nameNp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name_en", columnDefinition = "VARCHAR(50)")
    private EnglishDayType nameEn;
}
