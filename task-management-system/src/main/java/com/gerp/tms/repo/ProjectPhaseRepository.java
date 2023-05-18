package com.gerp.tms.repo;

import com.gerp.tms.model.phase.Phase;
import com.gerp.tms.model.project.Project;
import com.gerp.tms.model.project.ProjectPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectPhaseRepository extends JpaRepository<ProjectPhase, Long> {
    ProjectPhase findByProjectAndPhase(Project project, Phase phase);
}
