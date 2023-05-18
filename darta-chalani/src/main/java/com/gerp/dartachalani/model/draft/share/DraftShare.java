package com.gerp.dartachalani.model.draft.share;

import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "draft_share")
public class DraftShare extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_draft_share_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_draft_share_seq_gen", sequenceName = "seq_dispatch_letter_draft_share", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "sender_pis_code", nullable = false)
    private String senderPisCode;

    @Column(name = "sender_section_code", nullable = false)
    private String senderSectionCode;

    @Column(name = "receiver_pis_code", nullable = false)
    private String receiverPisCode;

    @Column(name = "receiver_section_code", nullable = false)
    private String receiverSectionCode;

    @Enumerated(EnumType.STRING)
    private Status status = Status.P;

    @Enumerated(EnumType.STRING)
    private DcTablesEnum letterType;

    @Column(name = "dispatch_id")
    private Long dispatchId;

    @Column(name = "memo_id")
    private Long memoId;

}
