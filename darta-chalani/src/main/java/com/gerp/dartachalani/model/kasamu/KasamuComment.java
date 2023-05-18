package com.gerp.dartachalani.model.kasamu;

import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import com.gerp.shared.generic.api.AuditAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KasamuComment extends AuditAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kasamu_comment_seq_gen")
    @SequenceGenerator(name = "kasamu_comment_seq_gen", sequenceName = "seq_kasamu_comment", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "comment", columnDefinition = "VARCHAR(1000)")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "kasamu_state_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_kasamu_state__kasamu_comment"))
    private KasamuState kasamuState;
}
