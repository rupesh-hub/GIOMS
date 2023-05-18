package com.gerp.dartachalani.model.dispatch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "dispatch_letter_forward")
public class DispatchLetterForward extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_forward_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_forward_seq_gen", sequenceName = "seq_dispatch_letter_forward", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "description",  columnDefinition = "VARCHAR(200)")
    private String description;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "dispatch_letter_id")
//    private DispatchLetter dispatchLetter;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_forward_id", foreignKey = @ForeignKey(name = "FK_DispatchLetterForward_DispatchedLetterReceiver"))
    @JsonIgnore
    private Collection<DispatchedLetterReceiver> dispatchedLetterReceivers;
    @Column(name = "sender_section_id")
    private Long senderSectionId;
    @Column(name = "sender_pis_code", length = 20)
    private String senderPisCode;
    @Column(name = "receiver_pis_code", length = 20)
    private String receiverPisCode;
    @Column(name = "completion_status", length = 20)
    private String completionStatus;
}
