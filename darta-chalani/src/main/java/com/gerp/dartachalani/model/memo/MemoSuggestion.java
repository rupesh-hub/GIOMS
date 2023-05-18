package com.gerp.dartachalani.model.memo;

import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "memo_suggestion")
public class MemoSuggestion extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_suggestion_seq_gen")
    @SequenceGenerator(name = "memo_suggestion_seq_gen", sequenceName = "seq_memo_suggestion", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "approver_pis_code")
    private String approverPisCode;

    @Column(name = "sender_pis_code")
    private String senderPisCode;

    @Column(name = "sender_office_code")
    private String senderOfficeCode;

    @Column(name = "sender_section_code")
    private String senderSectionCode;

    @Column(name = "sender_designation_code")
    private String senderDesignationCode;

    @Column(name = "approver_office_code")
    private String approverOfficeCode;

    @Column(name = "approver_section_code")
    private String approverSectionCode;

    @Column(name = "approver_designation_code")
    private String approverDesignationCode;

    @Column(name = "remarks")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @Column(name = "initial")
    private Boolean initial = false;

    @Column(name = "log")
    private Long log;

    @Column(name = "initial_sender")
    private String initialSender;

    @Column(name = "first_sender")
    private String firstSender;

    @Column(name = "is_seen")
    private Boolean isSeen;

    @Column(name = "reverted")
    private Boolean reverted;

    @Column(name = "is_revert")
    private Boolean isRevert;

    @Column(name = "is_important")
    private Boolean isImportant;

    @ManyToOne
    @JoinColumn(name = "memo_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo_suggestion"))
    private Memo memo;

    @Column(name = "is_transferred")
    private Boolean isTransferred = Boolean.FALSE;
}
