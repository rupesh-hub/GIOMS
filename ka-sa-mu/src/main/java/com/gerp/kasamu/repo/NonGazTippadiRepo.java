package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.kasamu.NonGazettedSupervisorTippadi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NonGazTippadiRepo extends JpaRepository<NonGazettedSupervisorTippadi,Integer> {
}
