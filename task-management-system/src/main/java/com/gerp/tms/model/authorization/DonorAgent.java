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
public class DonorAgent  extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "donor_agent_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "donor_agent_seq_gen", sequenceName = "seq_donor_agent", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorAgentUcd;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorAgentNameE;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorAgentNameN;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Donor.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_agent_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_donor_agent_donor"))
    private List<Donor> donorList;
}
