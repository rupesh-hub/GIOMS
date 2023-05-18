package com.gerp.dartachalani.model.dispatch;

import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "dispatched_letter_receiver")
public class DispatchedLetterReceiver extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatched_letter_receiver_seq_gen")
    @SequenceGenerator(name = "dispatched_letter_receiver_seq_gen", sequenceName = "seq_despatched_letter_receiver", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "receiver_office_code", columnDefinition = "VARCHAR(20)")
    private String receiverOfficeCode;

    @Column(name = "description", columnDefinition = "VARCHAR(1000)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_forward_id")
    private DispatchLetterForward dispatchLetterForward;
    @Enumerated(EnumType.STRING)
    private LetterPriority letterPriority;

    @Enumerated(EnumType.STRING)
    private LetterPrivacy letterPrivacy;
    @Column(name = "sender_office_code", length = 20)
    private String senderOfficeCode;
    @Column(name = "registration_id")
    private Long registrationId;
    @Column(name = "reference_no")
    private String referenceNo;
    @Column(name = "dispatch_no")
    private Long dispatchNo;
    @Column(name = "dispatch_date_en")
    private LocalDate dispatchDateEn;
    @Column(name = "dispatch_date_np")
    private String dispatchDateNp;
    @Column(name = "subject")
    private String subject;
    @Column(name = "document_id")
    private String documentId;
    @Column(name = "entry_type")
    private String entryType;
    @Column(name = "is_draft")
    private boolean isDraft;

}
