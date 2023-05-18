package com.gerp.dartachalani.model.dispatch;

import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "dispatch_letter_review")
public class DispatchLetterReview extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_review_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_review_seq_gen", sequenceName = "seq_dispatch_review_letter", initialValue = 1, allocationSize = 1)
    private Long id;
    @Column(name = "receiver_office_name")
    private String receiverOfficeCode;
    @Column(name = "receiver_pis_code")
    private String receiverPisCode;

    @Column(name = "receiver_section_code")
    private String receiverSectionCode;

    @Column(name = "receiver_designation_code")
    private String receiverDesignationCode;

    @Column(name = "sender_pis_code")
    private String senderPisCode;

    @Column(name = "sender_section_code")
    private String senderSectionCode;

    @Column(name = "sender_designation_code")
    private String senderDesignationCode;

    @Column(name = "received_date")
    private LocalDate receivedDate;
    @Column(name = "received_date_np")
    private String receivedDateNp;
    private String subject;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String remarks;

    @Column(name = "is_seen")
    private Boolean isSeen = false;

    @Column(name = "reverted")
    private Boolean reverted = false;

    @Column(name = "remarks_signature")
    @Type(type = "org.hibernate.type.TextType")
    private String remarksSignature;

    @Column(name = "remarks_signature_is_active")
    private Boolean remarksSignatureIsActive;

    @Column(name = "content_log_id")
    private Long contentLogId;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "hash_content")
    private String hashContent;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", foreignKey = @ForeignKey(name = "FK_dispatch_letter_dispatchletterreview"))
    private DispatchLetter dispatchLetter;

    @Column(name = "is_transferred")
    private Boolean isTransferred = Boolean.FALSE;
}
