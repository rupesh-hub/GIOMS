package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "transfer_authority_type")
@NoArgsConstructor
public class TransferAuthorityType extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_authority_type_seq_gen")
    @SequenceGenerator(name = "transfer_authority_type_seq_gen", sequenceName = "transfer_authority_type_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)", name = "type")
    private String type;

    public TransferAuthorityType(String type) {
        this.type = type;
    }
}
