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
public class EconomicLevel extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "economic_level_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "economic_level_seq_gen", sequenceName = "seq_economic_level", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String economicLevelUcd;

    @Column(columnDefinition = "VARCHAR(20)")
    private String economicLevelNameE;

    @Column(columnDefinition = "VARCHAR(20)")
    private String economicLevelNameN;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = AuthorizationActivity.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "economic_level_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_economic_level_authorization_activity"))
    private List<AuthorizationActivity> authorizationActivityList;
}
