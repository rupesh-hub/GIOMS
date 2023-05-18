package com.gerp.dartachalani.model.dispatch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.ReceiverType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "dispatch_letter")
public class DispatchLetter extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_seq_gen", sequenceName = "seq_dispatch_letter", initialValue = 1, allocationSize = 1)
    private Long id;

    private String dispatchNo;

    @Column(name = "sender_pis_code", columnDefinition = "VARCHAR(20)")
    private String senderPisCode;

    @Column(name = "sender_office_code")
    private String senderOfficeCode;

    @Column(name = "sender_section_code")
    private String senderSectionCode;

    @Column(name = "sender_designation_code")
    private String senderDesignationCode;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "dispatch_date_en")
    private LocalDate dispatchDateEn;

    @Column(name = "dispatch_date_np", columnDefinition = "VARCHAR(20)")
    private String dispatchDateNp;

    @Column(name = "subject", columnDefinition = "VARCHAR(300)")
    private String subject;

    @Column(name = "is_draft")
    private Boolean isDraft;

    @Column(name = "signee", columnDefinition = "VARCHAR(200)")
    private String signee;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_id", foreignKey = @ForeignKey(name = "FK_DispatchLetter_DispatchLetterReceiverInternal"))
    @JsonIgnore
    private Collection<DispatchLetterReceiverInternal> dispatchLetterReceiverInternals;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_id", foreignKey = @ForeignKey(name = "FK_DispatchLetter_DispatchLetterReceiverExternal"))
    @JsonIgnore
    private Collection<DispatchLetterReceiverExternal> dispatchLetterReceiverExternals;

    @Enumerated(EnumType.STRING)
    private LetterPriority letterPriority;

    @Enumerated(EnumType.STRING)
    private LetterPrivacy letterPrivacy;

    @Enumerated(EnumType.STRING)
    private ReceiverType receiverType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @Type(type = "org.hibernate.type.TextType")
    private String remarks;

    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    @Column(name = "referencef_code")
    private String referenceCode;

    @Column(name = "singular")
    private Boolean singular;

    @Column(name = "include")
    private Boolean include;

    @Column(name = "is_english")
    private Boolean isEnglish = false;

    @Column(name = "is_ad")
    private Boolean isAd = false;

    @Column(name = "remarks_signature")
    @Type(type = "org.hibernate.type.TextType")
    private String remarksSignature;

    @Column(name = "remarks_signature_is_active")
    private Boolean remarksSignatureIsActive;

    @Column(name = "hash_content")
    private String hashContent;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "remarks_pis_code")
    private String remarksPisCode;

    @Column(name = "remarks_section_code")
    private String remarksSectionCode;

    @Column(name = "remarks_designation_code")
    private String remarksDesignationCode;

    @Column(name = "last_modified_date")
    private Timestamp lastModifiedDate;

    @Column(name = "last_modified_date_imp")
    private Timestamp lastModifiedDateImp;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_id", foreignKey = @ForeignKey(name = "FK_dispatch_letter_document_detail"))
    @JsonIgnore
    private Collection<DispatchLetterDocumentDetails> dispatchLetterDocumentDetails;

    @Column(name = "template_header_id")
    private Long templateHeaderId;

    @Column(name = "template_footer_id")
    private Long templateFooterId;

    @Column(name = "has_subject")
    private Boolean hasSubject = Boolean.TRUE;

    @Column(name = "is_archive")
    private Boolean isArchive = Boolean.FALSE;

    @Column(name = "is_already_approved")
    private Boolean isAlreadyApproved = Boolean.FALSE;

    public DispatchLetter(Long dispatchLetterId) {
        super();
    }

}
