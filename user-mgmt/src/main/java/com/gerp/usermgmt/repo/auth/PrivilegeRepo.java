package com.gerp.usermgmt.repo.auth;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.Privilege;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PrivilegeRepo extends GenericRepository<Privilege, Long> {

    @Query(value = "select * from privilege where privilege_key = ?1 ", nativeQuery = true)
    Optional<Privilege> findByKey(String name);
}
