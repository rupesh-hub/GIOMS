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
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "memo_forward_history")
public class MemoForwardHistory extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_forward_history_seq_gen")
    @SequenceGenerator(name = "memo_forward_history_seq_gen", sequenceName = "seq_memo_forward_history", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "designation_code")
    private String designationCode;

    @ManyToOne
    @JoinColumn(name = "memo_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo__memo_forward_history"))
    private Memo memo;

}
