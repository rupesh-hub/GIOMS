package com.gerp.dartachalani.model.memo;

import com.gerp.dartachalani.model.dispatch.DispatchLetter;
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
@DynamicUpdate
@Builder
@Table(name = "memo_reference")
public class MemoReference extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_reference_seq_gen")
    @SequenceGenerator(name = "memo_reference_seq_gen", sequenceName = "seq_memo_reference", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "referenced_memo_id")
    private Long referencedMemoId;

    @Column(name = "chalani_reference_id")
    private Long chalaniReferenceId;

    @Column(name = "darta_reference_id")
    private Long dartaReferenceId;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "include")
    private Boolean include = true;

    @Column(name = "is_editable")
    private Boolean isEditable;

    @Column(name = "is_attach")
    private Boolean isAttach;

    @ManyToOne
    @JoinColumn(name = "memo_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo__reference"))
    private Memo memo;

    @ManyToOne
    @JoinColumn(name = "dispatch_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_dispatch__reference"))
    private DispatchLetter dispatchLetter;

}
