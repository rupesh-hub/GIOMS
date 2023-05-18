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
public class ActivityLevel extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "activity_level_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "activity_level_seq_gen", sequenceName = "seq_activity_level", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String activityLevelUcd;

    @Column(columnDefinition = "VARCHAR(50)")
    private String activityLevelNameE;

    private Boolean isProject;
    @Column(columnDefinition = "VARCHAR(50)",name = "activity_level_namen")
    private String activityLevelNameN;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = AuthorizationActivity.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_level_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_activity_level_authorization_activity"))
    private List<AuthorizationActivity> authorizationActivityList;
}
