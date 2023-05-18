package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "transfer_config")
public class TransferConfig extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_config_seq_gen")
    @SequenceGenerator(name = "transfer_config_seq_gen", sequenceName = "seq_transfer_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(10)")
    private String officeCode;
    @Column(name = "type", columnDefinition = "VARCHAR(10)")
    private String type;
    @Column(name = "minister_code", columnDefinition = "VARCHAR(10)")
    private String ministerCode;
}
