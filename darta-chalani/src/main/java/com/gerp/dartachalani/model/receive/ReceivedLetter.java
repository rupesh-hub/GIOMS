package com.gerp.dartachalani.model.receive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;

import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "received_letter")
public class ReceivedLetter extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "received_letter_seq_gen")
    @SequenceGenerator(name = "received_letter_seq_gen", sequenceName = "seq_received_letter", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_priority")
    private LetterPriority letterPriority;

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_privacy")
    private LetterPrivacy letterPrivacy;

    @NotNull
    @NotBlank
    @Column(name = "office_code", columnDefinition = "VARCHAR(10)")
    private String officeCode;

    @Column(name = "pis_code", columnDefinition = "VARCHAR(15)")
    private String pisCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "designation_code")
    private String designationCode;

    @NotBlank
    @NotNull
    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    @Column(name = "sender_office_code", columnDefinition = "VARCHAR(10)")
    private String senderOfficeCode;

    @NotNull
    @Column(name = "registration_no")
    private String registrationNo;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "dispatch_no")
    private String dispatchNo;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "dispatch_date_en")
    private LocalDate dispatchDateEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "dispatch_date_np", columnDefinition = "VARCHAR(20)")
    private String dispatchDateNp;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
    @Column(name = "subject", columnDefinition = "VARCHAR(200)")
    private String subject;

    private Long documentMasterId;

    @Column(name = "entry_type")
    private Boolean entryType;

    @Column(name = "is_draft")
    private Boolean isDraft;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @OneToOne(mappedBy = "receivedLetter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ManualReceivedLetterDetail manualReceivedLetterDetail;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "received_letter_id", foreignKey = @ForeignKey(name = "FK_received_letter_document_detail"))
    @JsonIgnore
    private Collection<ReceivedLetterDocumentDetails> receivedLetterDocumentDetails;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Column(name = "dispatch_id")
    private Long dispatchId;

    @Column(name = "include")
    private Boolean include = false;

    @Column(name = "is_english")
    private Boolean isEnglish = false;

    @Column(name = "signature")
    @Type(type = "org.hibernate.type.TextType")
    private String signature;

    @Column(name = "signature_is_active")
    private Boolean signatureIsActive;

    @Type(type = "org.hibernate.type.TextType")
    private String remarks;

    @Column(name = "remarks_pis_code")
    private String remarksPisCode;

    @Column(name = "remarks_signature")
    @Type(type = "org.hibernate.type.TextType")
    private String remarksSignature;

    @Column(name = "remarks_signature_is_active")
    private Boolean remarksSignatureIsActive;

    @Column(name = "hash_content")
    private String hashContent;

    @Column(name = "is_singular")
    private Boolean isSingular;

    @Column(name = "is_receiver")
    private Boolean isReceiver;

    @Column(name = "salutation")
    private String salutation;

    @Column(name = "manual_is_cc")
    private Boolean manualIsCc;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "last_modified_date_imp")
    private Timestamp lastModifiedDateImp;

    @Column(name = "is_important_head")
    private Boolean isImportantHead;

    @Column(name = "last_modified_date_imp_head")
    private Timestamp lastModifiedDateImpHead;

    @Column(name = "template_header_id")
    private Long templateHeaderId;

    @Column(name = "template_footer_id")
    private Long templateFooterId;
}
