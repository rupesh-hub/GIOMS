package com.gerp.tms.model.task;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_progress_status")
public class TaskProgressStatus extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "task_progress_status_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_progress_status_seq_gen", sequenceName = "seq_progress_status", initialValue = 1, allocationSize = 1)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "status_name", columnDefinition = "VARCHAR(20)")
    private String statusName;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "status_name_np", columnDefinition = "VARCHAR(20)")
    private String statusNameNp;

    private Boolean deleteAble;

}
