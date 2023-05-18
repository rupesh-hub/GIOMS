package com.gerp.attendance.model.postAttendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "post_attendance_request_detail",
        indexes = @Index(name = "pa_index_approval_record", columnList = "record_id", unique = true))
public class PostAttendanceRequestDetail extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_attendance_request_detail_seq_gen")
    @SequenceGenerator(name = "post_attendance_request_detail_seq_gen", sequenceName = "seq_post_attendance_request_detail", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "from_date_en")
    private LocalDate fromDateEn;

    @Column(name = "to_date_en")
    private LocalDate toDateEn;

    @Column(name = "remarks")
    private String remarks;
//
//    @Column(name = "supporting_documents_id")
//    private Long supportingDocumentId;

    @Column(name = "from_date_np", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String fromDateNp;

    @Column(name = "to_date_np", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String toDateNp;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    private Long documentId;
    private String documentName;


    @Column(name = "record_id")
//    @GeneratorType(type = UniqueIdGenerator.class, when = GenerationTime.INSERT)
    private UUID recordId;

    @ManyToOne
    @JoinColumn(name = "post_attendance_request_id")
    private PostAttendanceRequest postAttendanceRequest;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_attendance_id", foreignKey = @ForeignKey(name = "FK_postAttendance_approval"))
    @JsonIgnore
    private Collection<DecisionApproval> postAttendanceApprovals;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_attendance_request_detail_id", foreignKey = @ForeignKey(name = "fk_post_attendance_request_detail_approval"))
//    private Collection<PostAttendanceRequestApproval> postAttendanceRequestApprovals;
}
