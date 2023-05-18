package com.gerp.tms.model.phase;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.model.AuditAbstractTms;
import com.gerp.tms.model.project.ProjectPhase;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Diwakar sah
 * @version 1.0.0
 * @since 1.0.0
 */


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@DynamicUpdate
@Table(name = "phase")
public class Phase extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "phase_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "phase_seq_gen", sequenceName = "seq_phase", initialValue = 1, allocationSize = 1)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "phase_name", columnDefinition = "VARCHAR(30)")
    private String phaseName;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "phase_name_np", columnDefinition = "VARCHAR(30)")
    private String phaseNameNp;


    @OneToMany(fetch = FetchType.LAZY, targetEntity = ProjectPhase.class)
    @JoinColumn(name = "phase_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_phase"))
    private List<ProjectPhase> projectPhaseList;
}
