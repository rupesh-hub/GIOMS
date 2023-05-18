package com.gerp.tms.model.authorization;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@DynamicUpdate
@Getter
@Setter
public class DonorSubGroup extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "donor_sub_group_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "donor_sub_group_seq_gen", sequenceName = "seq_donor_sub_group", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorSubGroupUcd;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorSubGroupNameE;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorSubGroupNameN;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = DonorAgent.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_sub_group_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_donor_sub_group_agent"))
    private List<DonorAgent> donorAgents;
}
