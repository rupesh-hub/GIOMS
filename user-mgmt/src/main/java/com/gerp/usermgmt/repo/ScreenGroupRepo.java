package com.gerp.usermgmt.repo;


import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.ScreenGroup;

import java.util.Optional;

public interface ScreenGroupRepo extends GenericRepository<ScreenGroup, Long> {
    Optional<ScreenGroup> findByKey(String key);
}
