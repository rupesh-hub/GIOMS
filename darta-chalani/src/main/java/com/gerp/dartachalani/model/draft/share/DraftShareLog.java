package com.gerp.dartachalani.model.draft.share;

import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.generic.api.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "draft_share_log")
public class DraftShareLog extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_draft_share_log_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_draft_share_log_seq_gen", sequenceName = "seq_dispatch_letter_draft_share_log", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status fromStatus;

    @Enumerated(EnumType.STRING)
    private Status toStatus;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "draft_share_id")
    private Long draftShareId;
}
