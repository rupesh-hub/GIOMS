package com.gerp.dartachalani.model.receive;

import com.gerp.dartachalani.model.memo.Memo;
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

@Entity
@Data
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "received_letter_memo")
public class ReceivedLetterMemo extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "received_letter_memo_seq_gen")
    @SequenceGenerator(name = "received_letter_memo_seq_gen", sequenceName = "seq_received_letter_memo", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "received_letter_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_received_letter_memo"))
    private ReceivedLetter receivedLetter;

    @ManyToOne
    @JoinColumn(name = "memo_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo_received_letter"))
    private Memo memo;

}
