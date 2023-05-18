package com.gerp.usermgmt.repo;

import com.gerp.usermgmt.model.ModuleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleKeyRepo extends JpaRepository<ModuleKey, Long> {
}
