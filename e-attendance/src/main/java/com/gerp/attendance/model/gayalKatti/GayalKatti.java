package com.gerp.attendance.model.gayalKatti;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@DynamicUpdate
@Table(name = "gayal_katti")
public class GayalKatti extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gayal_katti_seq_gen")
    @SequenceGenerator(name = "gayal_katti_seq_gen", sequenceName = "seq_gayal_katti", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(10)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String officeCode;

    @Column(name = "pis_code", columnDefinition = "VARCHAR(10)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String pisCode;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "from_date_en")
    private LocalDate fromDateEn;

    @Column(name = "from_date_np", columnDefinition = "VARCHAR(10)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String fromDateNp;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "to_date_en")
    private LocalDate toDateEn;

    @Column(name = "to_date_np", columnDefinition = "VARCHAR(10)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String toDateNp;

    @Column(name = "record_id")
//    @GeneratorType(type = UniqueIdGenerator.class, when = GenerationTime.INSERT)
    private UUID recordId;

    @Column(name = "remarks", columnDefinition = "VARCHAR(255)")
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String remarks;

//    @Enumerated(EnumType.STRING)
//    @Builder.Default
//    private Status status = Status.P;

    private Long documentMasterId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "gayal_katti_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_gayal_katti_document_details"))
    @JsonIgnore
    private Collection<GayalKattiDocumentDetails> gayalKattiDocumentDetails;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @JoinColumn(name = "gayal_katti_id", foreignKey = @ForeignKey(name = "FK_GayalKatti_DecisionApproval"))
//    @JsonIgnore
//    private Collection<DecisionApproval> gayalKattiApprovals;
}
