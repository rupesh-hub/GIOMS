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
public class DonorGroup extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "donor_group_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "donor_group_seq_gen", sequenceName = "seq_donor_group", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorGroupUcd;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorGroupNameE;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorGroupNameN;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = DonorSubGroup.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_group_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_donor_group_sub_group"))
    private List<DonorSubGroup> donorSubGroupList;
}
