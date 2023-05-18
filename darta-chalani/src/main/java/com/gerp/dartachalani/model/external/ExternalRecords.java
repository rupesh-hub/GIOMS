package com.gerp.dartachalani.model.external;

import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.model.receive.ReceivedLetter;
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
@Table(name = "external_records")
public class ExternalRecords extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "external_records_seq_gen")
    @SequenceGenerator(name = "external_records_seq_gen", sequenceName = "seq_external_records", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo__external"))
    private Memo memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_letter_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_received_letter__external"))
    private ReceivedLetter receivedLetter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", foreignKey = @ForeignKey(name = "FK_dispatch_letter__external"))
    private DispatchLetter dispatchLetter;

}
