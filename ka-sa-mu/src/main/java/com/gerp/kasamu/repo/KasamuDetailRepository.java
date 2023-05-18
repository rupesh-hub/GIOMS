package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.kasamu.KasamuDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KasamuDetailRepository extends JpaRepository<KasamuDetail,Long> {
}
