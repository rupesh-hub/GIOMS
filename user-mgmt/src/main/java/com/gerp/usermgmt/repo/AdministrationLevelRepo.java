package com.gerp.usermgmt.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.administrative.AdministrationLevel;

public interface AdministrationLevelRepo extends GenericSoftDeleteRepository<AdministrationLevel, Integer> {
}
