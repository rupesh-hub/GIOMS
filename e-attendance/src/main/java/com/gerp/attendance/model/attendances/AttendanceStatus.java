package com.gerp.attendance.model.attendances;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "attendance_status")
public class AttendanceStatus extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_status_seq_gen")
    @SequenceGenerator(name = "attendance_status_seq_gen", sequenceName = "seq_attendance_status", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(name = "name_en", columnDefinition = "VARCHAR(10)")
    private String nameEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(name = "name_np", columnDefinition = "VARCHAR(10)")
    private String nameNp;

    @Size(max = StringConstants.DEFAULT_MIN_SIZE_4)
    @Column(name = "code", columnDefinition = "VARCHAR(4)")
    private String code;
}
