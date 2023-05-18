package com.gerp.tms.repo;

import com.gerp.tms.model.task.TaskMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMemberRepository extends JpaRepository<TaskMember, Long> {
}
