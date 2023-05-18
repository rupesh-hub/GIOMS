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
@NoArgsConstructor
@Table(name = "transfer_authority_group")
public class TransferAuthorityGroup extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_authority_group_seq_gen")
    @SequenceGenerator(name = "transfer_authority_group_seq_gen", sequenceName = "transfer_authority_group_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "group_code",columnDefinition = "VARCHAR(10)")
    private String groupCode;

    public TransferAuthorityGroup(String groupCode) {
        this.groupCode = groupCode;
    }
}
