package com.gerp.dartachalani.model.receive;

import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "received_letter_message")
public class ReceivedLetterMessage extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "received_letter_message_seq_gen")
    @SequenceGenerator(name = "received_letter_message_seq_gen", sequenceName = "seq_received_letter_message", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "comment", columnDefinition = "VARCHAR(1000)")
    private String comment;

    @Column(name = "pis_code")
    private String pisCode;

    @ManyToOne
    @JoinColumn(name = "received_letter_forward_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_received_letter_forward__received_letter_message"))
    private ReceivedLetterForward receivedLetterForward;

}
