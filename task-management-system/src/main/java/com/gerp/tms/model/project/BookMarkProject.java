package com.gerp.tms.model.project;

import com.gerp.shared.generic.api.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@DynamicUpdate
@Getter
@Setter
public class BookMarkProject implements BaseEntity {

    @Id
    @GeneratedValue(generator = "project_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "project_seq_gen", sequenceName = "seq_project", initialValue = 1, allocationSize = 1)
    private Long id;

    private Boolean isBookedMarked;

    private String memberId;

    private Integer projectId;
}
