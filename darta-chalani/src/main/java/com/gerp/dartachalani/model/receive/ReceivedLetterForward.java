package com.gerp.dartachalani.model.receive;

import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

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
@Table(name = "received_letter_forward")
public class ReceivedLetterForward extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "received_letter_forward_seq_gen")
    @SequenceGenerator(name = "received_letter_forward_seq_gen", sequenceName = "seq_received_letter_forward", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "sender_section_id")
    private String senderSectionId;

    @Column(name = "sender_pis_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String senderPisCode;

    @Column(name = "sender_designation_code")
    private String senderDesignationCode;

    @Column(name = "receiver_section_id")
    private String receiverSectionId;

    @Column(name = "receiver_pis_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String receiverPisCode;

    @Column(name = "receiver_designation_code")
    private String receiverDesignationCode;

    @NotBlank
    @Column(name = "description", columnDefinition = "VARCHAR(1000)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status completion_status = Status.P;

    @ManyToOne
    @JoinColumn(name = "received_letter_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_received_letter__received_letter_forward"))
    private ReceivedLetter receivedLetter;

    @Column(name = "is_received")
    private Boolean isReceived;

    @Column(name = "to_cc")
    private Boolean toCc;

    @Column(name = "is_cc")
    private Boolean isCc = false;

    @Column(name = "is_seen")
    private Boolean isSeen = false;

    @Column(name = "is_reverted")
    private Boolean isReverted;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "sender_parent_code", columnDefinition = "VARCHAR(20)")
    private String senderParentCode;

    @Column(name = "last_status_changed_at")
    private Timestamp lastStatusChangedAt;

    @Column(name = "is_transferred")
    private Boolean isTransferred = Boolean.FALSE;
}
