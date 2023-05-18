package com.gerp.attendance.model.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.setup.PeriodicHoliday;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "leave_request_detail",
        indexes = @Index(name = "lr_index_approval_record", columnList = "record_id", unique = true))
public class LeaveRequestDetail extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requested_days_seq_gen")
    @SequenceGenerator(name = "requested_days_seq_gen", sequenceName = "seq_requested_days", initialValue = 1, allocationSize = 1)
    private Long id;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    @Enumerated(EnumType.STRING)
    private DurationType leaveFor;

    private String pisCode;
    private Integer groupOrder;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id")
    private LeaveRequest leaveRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_policy_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_LeaveRequest_LeavePolicy"))
    private LeavePolicy leavePolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodic_holiday_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_LeaveRequest_PeriodicHoliday"))
    private PeriodicHoliday periodicHoliday;

    private Integer travelDays;

    @Column(precision = 6, scale = 1)
    private Double actualLeaveDays;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "leaveRequestDetail", orphanRemoval = true, fetch = FetchType.LAZY)
//    @JoinColumn(name = "detail_id", foreignKey = @ForeignKey(name = "FK_leave_detail_documents"))
    private Collection<LeaveRequestDocument> document;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

//    private Long documentId;
//    private String documentName;
//    private Double documentSize;

    @Builder.Default
    private Long leaveApproveDartaNo = Long.valueOf(0);

    @Column(name = "record_id")
    private UUID recordId;

    private String remarks;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_detail_id", foreignKey = @ForeignKey(name = "FK_leaverequestdetail_approval"))
    @JsonIgnore
    private Collection<DecisionApproval> leaveRequestApprovals;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "year", columnDefinition = "VARCHAR(10)")
    private String year;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_detail_id", foreignKey = @ForeignKey(name = "FK_LeaveDetail_Document"))
    @JsonIgnore
    private Collection<LeaveRequestDocument> leaveRequestDocuments;

    public void removeDocument(LeaveRequestDocument document){
        leaveRequestDocuments.remove(document);
    }

    public void addDocument(LeaveRequestDocument document) {
        leaveRequestDocuments.add(document);
    }

}
