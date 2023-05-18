package com.gerp.tms.model.report;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import com.gerp.tms.model.authorization.AuthorizationActivity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@DynamicUpdate
@Entity
@NoArgsConstructor
public class Months extends AuditAbstractTms {

    public Months(String nameEn, String nameNp) {
        this.nameEn = nameEn;
        this.nameNp = nameNp;
    }

    @Id
    @GeneratedValue(generator = "monthly_remakrs_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "monthly_remakrs_seq_gen", sequenceName = "seq_monthly_remakrs", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String nameEn;
    @Column(columnDefinition = "VARCHAR(20)")
    private String nameNp;

//    @OneToMany(cascade = CascadeType.ALL, targetEntity = MonthlyRemarks.class,fetch = FetchType.LAZY)
//    @JoinColumn(name = "month_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_month_monthly_remarks"))
//    private List<MonthlyRemarks> monthlyRemarksList;
}
