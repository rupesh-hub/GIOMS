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
@Table(name = "transfer_authority_position")
@NoArgsConstructor
public class TransferAuthorityPosition  extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_authority_position_seq_gen")
    @SequenceGenerator(name = "transfer_authority_position_seq_gen", sequenceName = "transfer_authority_position_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(10)", name = "position_code")
    private String positionCode;

    public TransferAuthorityPosition(String positionCode) {
        this.positionCode = positionCode;
    }
}
