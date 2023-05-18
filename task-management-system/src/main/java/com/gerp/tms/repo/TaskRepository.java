package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.task.Task;

public interface TaskRepository extends GenericSoftDeleteRepository<Task, Long> {
}
