package com.gerp.usermgmt.repo.auth;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.ModuleApiMapping;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleApiMappingRepo  extends GenericRepository<ModuleApiMapping, Long> {
}
