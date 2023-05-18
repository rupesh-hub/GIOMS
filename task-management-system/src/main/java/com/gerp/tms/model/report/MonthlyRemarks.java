package com.gerp.tms.model.report;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@DynamicUpdate
@Entity
public class MonthlyRemarks extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "monthly_remakrs_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "monthly_remakrs_seq_gen", sequenceName = "seq_monthly_remakrs", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(columnDefinition = "VARCHAR(20)")
    private String status;

    private Double budget;

    @ManyToOne(targetEntity = Months.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "month_id",foreignKey = @ForeignKey(name = "fk_month_monthly_remarks"))
    private Months months;

}
