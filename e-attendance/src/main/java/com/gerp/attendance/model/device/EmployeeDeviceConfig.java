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

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "employee_device_config")
public class EmployeeDeviceConfig extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_device_config_seq_gen")
    @SequenceGenerator(name = "employee_device_config_seq_gen", sequenceName = "seq_employee_device_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "emp_code_from_device", columnDefinition = "VARCHAR(20)")
    private String empCodeFromDevice;

    @ManyToOne
    @JoinColumn(name = "attendance_device_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_EmployeeDeviceConfig_AttendanceDevice"))
    private AttendanceDevice attendanceDevice;
}
