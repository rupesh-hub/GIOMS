package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.committee.CommitteeIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeIndicatorRepository extends JpaRepository<CommitteeIndicator,Long> {
}
