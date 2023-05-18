package com.gerp.tms.model.task;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_progress_status_log")
public class TaskProgressStatusLog extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "task_progress_status_log_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_progress_status_log_seq_gen", sequenceName = "seq_task_progress_status_log", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dynamic_progress_status_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_dynamic_progress_status"))
    private DynamicProgressStatus dynamicProgressStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_task_progress"))
    private Task task;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
