package com.gerp.dartachalani.model.kasamu;

import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
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
public class KasamuState extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kasamu_state_seq_gen")
    @SequenceGenerator(name = "kasamu_state_seq_gen", sequenceName = "seq_kasamu_state", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "sender_pis_code")
    private String senderPisCode;

    @Column(name = "sender_section_code")
    private String senderSectionCode;

    @Column(name = "sender_office_code")
    private String senderOfficeCode;

    @Column(name = "receiver_pis_code", columnDefinition = "VARCHAR(20)")
    private String receiverPisCode;

    @Column(name = "receiver_section_code")
    private String receiverSectionCode;

    @Column(name = "receiver_office_code")
    private String receiverOfficeCode;

    @Column(name = "description", columnDefinition = "VARCHAR(1000)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @ManyToOne
    @JoinColumn(name = "kasamu_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_kasamu__kasamu_state"))
    private Kasamu kasamu;

    @Column(name = "is_cc")
    private Boolean isCc = Boolean.FALSE;

    @Column(name = "is_seen")
    private Boolean isSeen = Boolean.FALSE;

}
