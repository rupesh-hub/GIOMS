package com.gerp.tms.repo;

import com.gerp.tms.model.task.TaskProgressStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskProgressStatusLogRepository extends JpaRepository<TaskProgressStatusLog,Integer > {
}
