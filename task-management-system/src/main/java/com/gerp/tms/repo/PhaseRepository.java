package com.gerp.tms.repo;

import com.gerp.tms.model.phase.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhaseRepository extends JpaRepository<Phase,Long> {

    Boolean existsByPhaseName(String phaseName);

    @Query("select p from Phase p where p.createdBy =:parseLong ")
    List<Phase> findAllByCreatedById(@Param("parseLong") long parseLong);

}
