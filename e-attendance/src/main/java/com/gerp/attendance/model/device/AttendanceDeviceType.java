package com.gerp.attendance.model.device;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "attendance_device_type")
public class AttendanceDeviceType extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_device_type_seq_gen")
    @SequenceGenerator(name = "attendance_device_type_seq_gen", sequenceName = "seq_attendance_device_type", initialValue = 1, allocationSize = 1)
    private Integer id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "attendance_device_type_en", columnDefinition = "VARCHAR(200)")
    private String attendanceDeviceTypeEn;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    @Column(name = "attendance_device_type_np", columnDefinition = "VARCHAR(200)")
    private String attendanceDeviceTypeNp;

//    @ManyToMany(mappedBy = "attendanceDeviceTypes")
//    private Collection<AttendanceDevice> attendanceDevices;
}
