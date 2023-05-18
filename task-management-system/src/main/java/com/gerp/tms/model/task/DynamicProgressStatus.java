package com.gerp.tms.model.task;


import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import com.gerp.tms.model.project.Project;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
@Table(name = "dynamic_progress_status")
public class DynamicProgressStatus extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "dynamic_progress_status_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "dynamic_progress_status_seq_gen", sequenceName = "seq_dynamic_progress_status", initialValue = 1, allocationSize = 1)
    private Integer id;

//    @OneToMany(fetch = FetchType.LAZY, targetEntity = TaskProgressStatusLog.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "dynamic_task_progress_status_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_dynamic_task_progress_status"))
//    private List<TaskProgressStatusLog> taskProgressStatusLogs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_progress_status_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_task_progress_status_dynamic_progress_status"))
    private TaskProgressStatus taskProgressStatus;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "project_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "FK_Project_dynamic_Progress_Status"))
//    private Project project;

    @Column(name = "order_status")
    private Integer orderStatus;

    private Boolean deleteStatus;
}
