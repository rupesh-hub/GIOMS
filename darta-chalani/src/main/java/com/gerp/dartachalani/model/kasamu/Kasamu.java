package com.gerp.dartachalani.model.kasamu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.dartachalani.enums.KasamuSubjectType;
import com.gerp.dartachalani.model.receive.ManualReceivedLetterDetail;
import com.gerp.dartachalani.model.receive.ReceivedLetterDocumentDetails;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.ReceiverType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Kasamu extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kasamu_seq_gen")
    @SequenceGenerator(name = "kasamu_seq_gen", sequenceName = "seq_kasamu", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_priority")
    private LetterPriority priority = LetterPriority.VH;

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_privacy")
    private LetterPrivacy privacy = LetterPrivacy.HC;

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type")
    private ReceiverType type = ReceiverType.WITHIN_ORGANIZATION;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "subject")
    private String subject;

    @Enumerated(EnumType.STRING)
    private KasamuSubjectType subjectType;

    @Column(name = "lock")
    private Boolean lock = Boolean.FALSE;

    @Column(name = "employee_pis_code")
    private String employeePisCode;

    @Column(name = "employee_section_code")
    private String employeeSectionCode;

    @Column(name = "employee_office_code")
    private String employeeOfficeCode;

    @Column(name = "registration_no")
    private String registrationNo;

    @Column(name = "fiscal_year_code")
    private String fiscalYearCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status completionStatus = Status.P;

    @Column(name = "is_external_employee")
    private Boolean isExternalEmployee = Boolean.FALSE;

    @OneToOne(mappedBy = "kasamu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ExternalKasamuEmployee externalKasamuEmployee;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_id", foreignKey = @ForeignKey(name = "FK_kasamu_document_detail"))
    @JsonIgnore
    private Collection<KasamuDocumentDetails> kasamuDocumentDetails;

}
