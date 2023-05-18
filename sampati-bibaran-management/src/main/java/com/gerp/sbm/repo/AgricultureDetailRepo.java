package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.AgricultureDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgricultureDetailRepo extends JpaRepository<AgricultureDetail,Long> {
    List<AgricultureDetail> findAllByPiscode(String piscode);
}
