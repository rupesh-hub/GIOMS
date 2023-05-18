package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.authorization.ActivityLevel;

public interface ActivityLevelRepo extends GenericSoftDeleteRepository<ActivityLevel,Integer> {
}
