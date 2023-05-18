package com.gerp.tms.model.project;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.model.AuditAbstractTms;
import com.gerp.tms.model.phase.Phase;
import com.gerp.tms.model.phase.PhaseMember;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Diwakar sah
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "project_phase")
public class ProjectPhase extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "project_phase_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "project_phase_seq_gen", sequenceName = "seq_project_phase", initialValue = 1, allocationSize = 1)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    @Column(name = "phase_description", columnDefinition = "VARCHAR(255)")
    private String phaseDescription;

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

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "phase_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_phase"))
   private Phase phase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_project"))
    private Project project;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_phase_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_Project_Phase"))
    private List<PhaseMember> phaseMemberList;

    private Boolean active;
}
