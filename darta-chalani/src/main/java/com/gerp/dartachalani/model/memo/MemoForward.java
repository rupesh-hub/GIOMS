package com.gerp.dartachalani.model.memo;

import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "memo_forward")
public class MemoForward extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_forward_seq_gen")
    @SequenceGenerator(name = "memo_forward_seq_gen", sequenceName = "seq_memo_forward", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "sender_section_id")
    private String senderSectionId;

    @Column(name = "sender_pis_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String senderPisCode;

    @Column(name = "sender_office_code")
    private String senderOfficeCode;

    @Column(name = "receiver_office_code")
    private String receiverOfficeCode;

    @Column(name = "receiver_section_id")
    private String receiverSectionId;

    @Column(name = "receiver_pis_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String receiverPisCode;

    @Column(name = "receiver_designation_code")
    private String receiverDesignationCode;

    @Column(name = "description", columnDefinition = "VARCHAR(1000)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status completion_status = Status.P;

    @ManyToOne
    @JoinColumn(name = "memo_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo__memo_forward"))
    private Memo memo;
}
