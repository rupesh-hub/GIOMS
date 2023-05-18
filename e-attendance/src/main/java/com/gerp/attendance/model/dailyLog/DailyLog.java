package com.gerp.attendance.model.dailyLog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Data
@Table(name = "daily_log",
        indexes = @Index(name = "dl_index_approval_record", columnList = "record_id", unique = true))
public class DailyLog extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_log_seq_gen")
    @SequenceGenerator(name = "daily_log_seq_gen", sequenceName = "seq_daily_log", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String officeCode;

    @Column(name = "pis_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    private String pisCode;

    @Column(name = "date_en")
    private LocalDate dateEn;

    @Column(name = "date_np", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String dateNp;

    @Column(name = "in_time")
    private LocalTime inTime;

    @Column(name = "out_time")
    private LocalTime outTime;

    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    private String fiscalYearCode;

    @Column(name = "remarks", columnDefinition = "VARCHAR(255)")
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String remarks;

    @Column(name = "location", columnDefinition = "VARCHAR(255)")
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String location;

    @Column(name = "record_id")
    private UUID recordId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id", foreignKey = @ForeignKey(name = "FK_dailylogs_approval"))
    @JsonIgnore
    private Collection<DecisionApproval> dailyLogApprovals;

    @Type(type = "org.hibernate.type.TextType")
    private String dailyLogRequesterHashContent;

    @Type(type = "org.hibernate.type.TextType")
    private String dailyLogRequesterSignature;

    @Type(type = "org.hibernate.type.TextType")
    private String content;

}
