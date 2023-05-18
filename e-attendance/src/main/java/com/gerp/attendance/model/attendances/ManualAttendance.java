package com.gerp.attendance.model.attendances;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.attendance.Pojo.VerificationInformation;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "manual_attendance")
public class ManualAttendance extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_attendance_seq_gen")
    @SequenceGenerator(name = "manual_attendance_seq_gen", sequenceName = "seq_manual_attendance", initialValue = 1, allocationSize = 1)
    private Long id;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(10)")
    private String officeCode;

    @Builder.Default
    private Boolean appliedForOthers = Boolean.FALSE;

    //    private String approverCode;
    private String pisCode;

    @Column(name = "fiscal_year_code")
    private Long fiscalYearCode;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "date_en")
    private LocalDate dateEn;

    private String dateNp;

    private Long documentId;

    private String documentName;

    private UUID recordId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
    @Column(name = "remarks", columnDefinition = "VARCHAR(200)")
    private String remarks;

//    private Boolean approvalStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_attendance_id", foreignKey = @ForeignKey(name = "FK_ManualAttendance_ManualAttendanceDetail"))
    @JsonIgnore
    private Collection<ManualAttendanceDetail> manualAttendanceDetails;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_attendance_id", foreignKey = @ForeignKey(name = "FK_manualattendance_approval"))
    @JsonIgnore
    private Collection<DecisionApproval> manualAttendanceApprovals;

    private String manualAttendanceRequestHashContent;
    private String content;
    private String manualAttendanceRequestSignature;
    /**
     * new manual attendance documents
     */
    private Long documentMasterId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name="manual_attendance_document",
            joinColumns = @JoinColumn( name="manual_attendance_id"),
            inverseJoinColumns = @JoinColumn( name="manual_att_document_details_id")
    )
    @JsonBackReference
    private Collection<ManualAttDocuments> manualAttDocumentDetails;


}
