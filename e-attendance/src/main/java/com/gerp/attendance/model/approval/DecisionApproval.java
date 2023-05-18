package com.gerp.attendance.model.approval;

import com.gerp.attendance.model.attendances.ManualAttendance;
import com.gerp.attendance.model.dailyLog.DailyLog;
import com.gerp.attendance.model.gayalKatti.GayalKatti;
import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.model.leave.LeaveRequest;
import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequestDetail;
import com.gerp.attendance.util.DelegationAbstract;
import com.gerp.shared.enums.Status;
import com.gerp.shared.enums.TableEnum;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "decision_approval"
//        ,
//        indexes = @Index(name = "da_index_approval_record", columnList = "record_id", unique = true)
)
public class DecisionApproval extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "decision_approval_seq_gen")
    @SequenceGenerator(name = "decision_approval_seq_gen", sequenceName = "seq_decision_approval", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(columnDefinition = "VARCHAR(6)")
    private TableEnum code;

    @Column(name = "record_id")
//    @GeneratorType(type = RecordIdGenerator.class, when = GenerationTime.INSERT)
    private UUID recordId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(columnDefinition = "VARCHAR(6)")
    private Status status = Status.P;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(columnDefinition = "VARCHAR(10)")
    private Status inActiveStatus = Status.P;

    @Column(name = "approver_pis_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String approverPisCode;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_detail_id")
    private LeaveRequestDetail leaveRequestDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kaaj_request_id")
    private KaajRequest kaajRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id")
    private DailyLog dailyLog;

    @NotNull
    private Boolean isApprover = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_attendance_id")
    private ManualAttendance manualAttendance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TA")
    private GayalKatti gayalKatti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_attendance_id")
    private PostAttendanceRequestDetail postAttendanceRequestDetail;

    private Long documentId;
    private String documentName;
    private Double documentSize;

    @Column(name = "signature")
    @Type(type = "org.hibernate.type.TextType")
    private String signature;

    @Column(name = "hash_content")
    @Type(type = "org.hibernate.type.TextType")
    private String hashContent;

    @Column(name = "content")
    @Type(type = "org.hibernate.type.TextType")
    private String content;
}
