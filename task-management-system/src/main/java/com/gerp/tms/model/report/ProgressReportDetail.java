package com.gerp.tms.model.report;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@DynamicUpdate
@Entity
public class ProgressReportDetail  extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "progress_report_detail_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "progress_report_detail_seq_gen", sequenceName = "seq_progress_report_detail", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Double budget;

    private Long taskId;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(columnDefinition = "VARCHAR(10)")
    private String approverCode;

    @Column(columnDefinition = "VARCHAR(10)")
    private String approvalStatus;

    @Column(columnDefinition = "TEXT")
    private String remarksByApprover;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = MonthlyRemarks.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_report_detail_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_progress_report_detail_monthly_remarks"))
    private List<MonthlyRemarks> monthlyRemarksList;
}
