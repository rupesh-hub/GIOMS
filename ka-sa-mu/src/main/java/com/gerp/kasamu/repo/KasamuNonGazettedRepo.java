package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.kasamu.KasamuForNoGazetted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KasamuNonGazettedRepo extends JpaRepository<KasamuForNoGazetted,Long> {
}
