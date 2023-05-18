package com.gerp.tms.model.phase;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.tms.model.AuditAbstractTms;
import com.gerp.tms.model.project.Project;
import com.gerp.tms.model.project.ProjectPhase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Diwakar sah
 * @version 1.0.0
 * @since 1.0.0
 */


@Entity
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "phase_member")
public class PhaseMember extends AuditAbstractTms {

    public PhaseMember(String memberId) {
        this.memberId = memberId;
    }

    public PhaseMember(String memberId, ProjectPhase projectPhase) {
        this.memberId = memberId;
        this.projectPhase = projectPhase;
    }

    @Id
    @GeneratedValue(generator = "phase_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "phase_seq_gen", sequenceName = "seq_phase", initialValue = 1, allocationSize = 1)
    private Long id;

    private String memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_phase_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_Project_Phase"))
    private ProjectPhase projectPhase;
}
