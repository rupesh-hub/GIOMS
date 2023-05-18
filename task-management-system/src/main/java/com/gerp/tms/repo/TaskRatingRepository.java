package com.gerp.tms.repo;

import com.gerp.tms.model.task.TaskRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRatingRepository extends JpaRepository<TaskRating,Long> {
    Optional<TaskRating> findByTaskId(long taskId);
}
