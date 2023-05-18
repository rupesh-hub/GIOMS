package com.gerp.tms.model.committee;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.model.AuditAbstractTms;
import com.gerp.tms.model.project.Project;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Entity
@Setter
@Getter
@DynamicUpdate
@Table(name = "committee")
public class Committee extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "committee_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "committee_seq_gen", sequenceName = "seq_committee", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "committee_name", columnDefinition = "VARCHAR(30)")
    private String committeeName;

    @Column(name = "committee_name_np", columnDefinition = "VARCHAR(30)")
    private String committeeNameNp;

   // @NotNull
    @OneToMany(fetch = FetchType.LAZY, targetEntity = Project.class)
    @JoinColumn(name = "committee_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_project_committee"))
    private List<Project> projects;

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, targetEntity = CommitteeMembers.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "committee_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_committee"))
    private List<CommitteeMembers> committeeMembers;
}
