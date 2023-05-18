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
public class Account extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "account_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "account_seq_gen", sequenceName = "seq_account", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String accountUcd;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = AuthorizationActivity.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_account_authorization_activity"))
    private List<AuthorizationActivity> authorizationActivityList;
}
