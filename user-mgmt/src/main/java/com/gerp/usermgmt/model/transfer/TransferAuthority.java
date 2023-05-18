package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "transfer_authority")
public class TransferAuthority extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_authority_seq_gen")
    @SequenceGenerator(name = "transfer_authority_seq_gen", sequenceName = "transfer_authority_config", initialValue = 1, allocationSize = 1)
    private Integer id;
    @Column(name = "service_code",columnDefinition = "VARCHAR(10)")
    private String serviceCode;


    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_authority_id", foreignKey = @ForeignKey(name = "fk_transfer_authority_offices"))
    private List<TransferAuthorityOffice> transferAuthorityOffices;

    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_authority_id", foreignKey = @ForeignKey(name = "fk_transfer_authority_positions"))
    private List<TransferAuthorityPosition> transferAuthorityPositions;

    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_authority_id", foreignKey = @ForeignKey(name = "fk_transfer_authority_type"))
    private List<TransferAuthorityType> transferAuthorityTypes;

    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_authority_id", foreignKey = @ForeignKey(name = "fk_transfer_authority_group"))
    private List<TransferAuthorityGroup> transferAuthorityGroups;
}
