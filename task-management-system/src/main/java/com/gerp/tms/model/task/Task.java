package com.gerp.tms.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "task")
public class Task extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "task_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_seq_gen", sequenceName = "seq_task", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true,columnDefinition = "VARCHAR(50)")
    private String code;

    @Column(name = "task_name", columnDefinition = "VARCHAR(255)")
    private String taskName;

    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    @Column(name = "task_description", columnDefinition = "VARCHAR(254)")
    private String taskDescription;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "priority", columnDefinition = "VARCHAR(20)")
    private String priority;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(name = "start_date_np", columnDefinition = "VARCHAR(10)")
    private String startDateNp;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(name = "end_date_np", columnDefinition = "VARCHAR(10)")
    private String endDateNp;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "is_responded")
    private Boolean isResponded;

    @Column(name = "response_by")
    private Integer responseBy;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "phase_id")
    private Long phaseId;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(name="task_status")
    private String taskStatus;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name="progress_status_id")
    private Long progressStatus;

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = TaskMember.class)
    @JoinColumn(name = "task_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_task_member"))
    private List<TaskMember> taskMembers;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = TaskProgressStatusLog.class)
    @JoinColumn(name = "task_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_task_progress"))
    private List<TaskProgressStatusLog> taskProgressStatusLogs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "FK_Task_Document"))
    @JsonIgnore
    private Collection<TaskDocumentDetails> taskDocumentDetails;
}
