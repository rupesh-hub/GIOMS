package com.gerp.attendance.model.attendances;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "attendance_type")
public class AttendanceType extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_type_seq_gen")
    @SequenceGenerator(name = "attendance_type_seq_gen", sequenceName = "seq_attendance_type", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name_np", columnDefinition = "VARCHAR(50)")
    private String nameNp;

    @Column(name = "name_en", columnDefinition = "VARCHAR(50)")
    private String nameEn;

    @Transient
    private Long testId;
}
