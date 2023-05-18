package com.gerp.usermgmt.repo.auth;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends GenericSoftDeleteRepository<User, Long> {

    @Query(value = "select * from users where user_name = ?1 ", nativeQuery = true)
    User findByName(String name);

    @Query(value = "select * from users where user_name = ?1 ", nativeQuery = true)
    Optional<User> findByUsername(String name);

    @Query(value = "select * from users where email = ?1 or user_name=?1", nativeQuery = true)
    User findByUsernameOrEmail(String usernameOrEmail);

    User findByPisEmployeeCode(String employeeCode);

    @Transactional
    @Modifying
    @Query(value = "update users set last_modified_date=current_timestamp, " +
            "is_active= case when is_active=true then false else true end\n" +
            "where id = ?1",nativeQuery = true)
    void deleteByUserId(Long id);

    List<User> findAllByCreatedBy(Long id);

}
