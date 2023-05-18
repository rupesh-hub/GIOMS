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
public class SourceType extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "source_type_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "source_type_seq_gen", sequenceName = "seq_source_type", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String sourceTypeUcd;

    @Column(columnDefinition = "VARCHAR(20)")
    private String sourceTypeNameE;

    @Column(columnDefinition = "VARCHAR(20)")
    private String sourceTypeNameN;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = AuthorizationActivity.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "source_type_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_source_type_authorization_activity"))
    private List<AuthorizationActivity> authorizationActivityList;
}
