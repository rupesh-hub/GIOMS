package com.gerp.dartachalani.model.manual;

import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.dartachalani.model.receive.ReceivedLetter;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "manually_received_letter")
public class ManuallyReceivedLetter extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manually_received_letter_seq_gen")
    @SequenceGenerator(name = "manually_received_letter_seq_gen", sequenceName = "seq_manually_received_letter", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "is_draft")
    private Boolean isDraft;

    @Column(name = "manual_received_letter_type", columnDefinition = "VARCHAR(20)")
    private String manualReceivedLetterType;

    @ManyToOne
    @JoinColumn(name = "received_letter_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_received_letter__manually_received_letter"))
    private ReceivedLetter receivedLetter;

//    @ManyToOne
//    @JoinColumn(name = "letter_privacy_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_letter_privacy__received_letter"))
//    private LetterPrivacy letterPrivacy;
//
//    @ManyToOne
//    @JoinColumn(name = "letter_priority_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_letter_priority__received_letter"))
//    private LetterPriority letterPriority;

    @Enumerated(EnumType.STRING)
    private LetterPriority letterPriority;


    @Enumerated(EnumType.STRING)
    private LetterPrivacy letterPrivacy;


}
