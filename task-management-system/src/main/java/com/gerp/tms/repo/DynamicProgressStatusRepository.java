package com.gerp.tms.repo;

import com.gerp.tms.model.task.DynamicProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicProgressStatusRepository extends JpaRepository<DynamicProgressStatus,Integer> {
}
