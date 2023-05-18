package com.gerp.attendance.model.device;

import com.gerp.shared.enums.AttendanceDeviceStatus;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "attendance_device", uniqueConstraints = @UniqueConstraint(name = "unique_name_attendancedevice", columnNames = {"device_name","macAddress"}))
public class AttendanceDevice extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_device_seq_gen")
    @SequenceGenerator(name = "attendance_device_seq_gen", sequenceName = "seq_attendance_device", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String officeCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Column(name = "device_name", columnDefinition = "VARCHAR(50)")
    private String deviceName;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Column(name = "device_model", columnDefinition = "VARCHAR(50)")
    private String deviceModel;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "device_serial_no", columnDefinition = "VARCHAR(20)")
    private String deviceSerialNo;

    private Integer port;

    private Integer deviceMachineNo;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "ip", columnDefinition = "VARCHAR(20)")
    private String ip;

    private String macAddress;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AttendanceDeviceStatus status = AttendanceDeviceStatus.NA;

    @ManyToOne
    @JoinColumn(name = "attendance_device_vendor_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_attendance_device__vendor_device"))
    private DeviceVendor deviceVendor;


}
