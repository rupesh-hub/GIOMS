package com.gerp.dartachalani.model.receive;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gerp.shared.enums.SenderType;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "manual_received_letter_detail")
public class ManualReceivedLetterDetail extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_received_letter_sender_detail_seq_gen")
    @SequenceGenerator(name = "manual_received_letter_sender_detail_seq_gen", sequenceName = "seq_manual_received_letter_sender_detail", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "sender_name", columnDefinition = "VARCHAR(250)")
    private String senderName;

    @Column(name = "sender_office_section_subsection", columnDefinition = "VARCHAR(250)")
    private String sectionOfficeSectionSubsection;

    @Column(name = "sender_address", columnDefinition = "VARCHAR(500)")
    private String senderAddress;

    @Column(name = "sender_email", columnDefinition = "VARCHAR(500)")
    private String senderEmail;

    @Column(name = "sender_phone_no", columnDefinition = "VARCHAR(250)")
    private String senderPhoneNo;

    @Column(name = "manual_received_letter_type")
    @Enumerated(EnumType.STRING)
    private SenderType manualReceivedLetterType;

    @Column(name = "section_code")
    private String sectionCode;

    @OneToOne
    @JoinColumn(name = "received_letter_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_received_letter_manual"))
    @JsonBackReference
    private ReceivedLetter receivedLetter;
}
