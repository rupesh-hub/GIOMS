package com.gerp.attendance.model.device;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "attendance_device_vendor")
public class DeviceVendor extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_vendor_seq_gen")
    @SequenceGenerator(name = "device_vendor_seq_gen", sequenceName = "seq_device_vendor", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "code", columnDefinition = "VARCHAR(10)")
    @Size(max = 10)
    private String code;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    private String name;

}
