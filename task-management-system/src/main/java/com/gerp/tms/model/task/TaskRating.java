package com.gerp.tms.model.task;

import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "task_rating")
public class TaskRating {

    @Id
    @GeneratedValue(generator = "task_rating_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_rating_seq_gen", sequenceName = "seq_task_rating", initialValue = 1, allocationSize = 1)
    private Long id;

    private Long taskId;

    private Integer rating;

    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    @Column(name = "description", columnDefinition = "VARCHAR(254)")
    private String description;
}
