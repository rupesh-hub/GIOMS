package com.gerp.tms.model.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.tms.model.AuditAbstractTms;
import com.gerp.tms.model.committee.Committee;
import com.gerp.tms.model.task.DynamicProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
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
@Table(name = "project")
public class Project  extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "project_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "project_seq_gen", sequenceName = "seq_project", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Column(name = "project_name", columnDefinition = "VARCHAR(100)")
    private String projectName;

    @NotNull
    @Column(name = "code", nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    private String code;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "office_id",columnDefinition = "VARCHAR(10)")
    private String officeId;

    @Column(name = "is_committee")
    private Boolean isCommittee;


    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

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

    @Column(name = "is_responded")
    private Boolean isResponded;

    @Column(name = "response_by")
    private Integer responseBy;

    @Column(name = "status")
    private String status;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    private String colorSchema;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_project"))
    @JsonIgnore
    private List<ProjectPhase> projectPhaseList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "FK_Project_Document"))
    @JsonIgnore
    private Collection<ProjectDocumentDetails> projectDocumentDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_project_committee"))
    private Committee committee;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "FK_Project_dynamic_Progress_Status"))
    private List<DynamicProgressStatus> dynamicProgressStatuses;
}