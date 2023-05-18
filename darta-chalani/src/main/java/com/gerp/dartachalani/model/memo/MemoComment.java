package com.gerp.dartachalani.model.memo;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "memo_comment")
public class MemoComment extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_comment_seq_gen")
    @SequenceGenerator(name = "memo_comment_seq_gen", sequenceName = "seq_memo_comment", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "comment", columnDefinition = "VARCHAR(1000)")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "memo_forward_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo_forward__comment"))
    private MemoForward memoForward;

}
