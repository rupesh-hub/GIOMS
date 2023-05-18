package com.gerp.tms.model.project;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
@Getter
@Setter
@Table(name = "project_category")
@DynamicUpdate
public class ProjectCategory extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "project_category_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "project_category_seq_gen", sequenceName = "seq_project_category", initialValue = 1, allocationSize = 1)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "category_name", columnDefinition = "VARCHAR(20)")
    private String categoryName;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "category_name_np", columnDefinition = "VARCHAR(20)")
    private String categoryNameNp;
}
