package com.gerp.usermgmt.model.device;

import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author info
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@DynamicUpdate
@Table(name = "piscode_device_id_mapper", uniqueConstraints = @UniqueConstraint(name = "unique_attendancemapper", columnNames = {"pis_code","office_code","device_id"}))
public class PisCodeDeviceIdMapper implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "piscode_deviceid_mapper_seq_gen")
    @SequenceGenerator(name = "piscode_deviceid_mapper_seq_gen", sequenceName = "seq_piscode_deviceid_mapper", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String officeCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(50)")
    private String pisCode;

    @Column(name = "device_id")
    private Long deviceId;

}
