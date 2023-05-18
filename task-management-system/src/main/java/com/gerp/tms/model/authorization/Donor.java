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
public class Donor extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "donor_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "donor_seq_gen", sequenceName = "seq_donor", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorNameN;

    @Column(columnDefinition = "VARCHAR(20)")
    private String donorNameE;

    @Column(columnDefinition = "VARCHAR(10)")
    private String donorCode;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = AuthorizationActivity.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_donor_authorization_activity"))
    private List<AuthorizationActivity> authorizationActivityList;
}
