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
public class AuthorizationActivity extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "authorization_activity_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "authorization_activity_seq_gen", sequenceName = "seq_authorization_activity", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(255)")
    private String targetedOutcomes;

    @Column(columnDefinition = "VARCHAR(10)")
    private String fiscalYearId;

    private Double amount;

    @Column(columnDefinition = "VARCHAR(10)")
    private String responsibleCode;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = SupportOffice.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "authorization_activity_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_authorization_activity_support_office"))

    private List<SupportOffice> supportOfficeList;

    @ManyToOne( targetEntity = PayingOffice.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "paying_office_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_paying_office_authorization_activity"))
    private PayingOffice payingOffice;

}
