package com.gerp.attendance.model.device;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance_device_log")
public class AttendanceDeviceLog extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_device_log_seq_gen")
    @SequenceGenerator(name = "attendance_device_log_seq_gen", sequenceName = "seq_attendance_device_log", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "recorded_date_time")
    private LocalDateTime recordedDateTime;

    @Column(name = "recorded_date")
    private LocalDate recordedDate;

    @Column(name = "recorded_time")
    private LocalTime recordedTime;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;

    @ManyToOne
    @JoinColumn(name = "attendance_device_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_attendance_device_log"))
    private AttendanceDevice attendanceDevice;

}
