package com.gerp.usermgmt.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.pojo.auth.RolePojo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoleGroupRepo extends GenericSoftDeleteRepository<RoleGroup, Long> {

    @Query(value = "select * from role_group where role_group_key = ?1", nativeQuery = true)
    Optional<RoleGroup> findByKey(String roleGroupKey);

    @Query(value = "select new com.gerp.usermgmt.pojo.auth.RolePojo(rg.id, rg.name) from RoleGroup rg where rg.key not in ('DEFAULT', 'SUPER_ADMIN' , 'SYSTEM_SCREEN_SETUP')")
    List<RolePojo> findRoleMiniMalDetail();

    @Query(value = "select ur.role_id from users us inner join users_roles ur on us.id = ur.user_id where us.user_name=?1", nativeQuery = true)
    List<Long> findRoleGroupByUsername(String username);

    @Query(value = "select * from role_group where role_group_key in (?1)", nativeQuery = true)
    List<Long> findRoleGroupIdByRoleGroupKeyIn(List<String> roleGroupNameList);

    @Query(nativeQuery = true, value = "select rg.*\n" +
            "from users us\n" +
            "         inner join users_roles ur on us.id = ur.user_id\n" +
            "         inner join role_group rg on ur.role_id = rg.id\n" +
            "where us.user_name =?1")
    List<RoleGroup> findRoleGroupListByUsername(String username);

    @Query(nativeQuery = true, value = "select rg.*" +
            "from" +
            " role_group rg ")
    List<RoleGroup> findAllRoleList();

    @Modifying
    @Transactional
    @Query(value = "delete from users_roles where user_id in ?1 and role_id = ?2 ", nativeQuery = true)
    void deleteUserRoleMappingByUserIds(List<Long> userIds, Long roleId);

    @Modifying
    @Transactional
    @Query(value = "delete from users_roles where user_id = ?1 and role_id = ?2 ", nativeQuery = true)
    void deleteUserRoleMappingByUserId(Long userId, Long roleId);

    @Modifying
    @Transactional
    @Query(value = "insert into users_roles(user_id, role_id) values(?1, ?2) ", nativeQuery = true)
    void assignUserRoleMappingByUserId(Long userId, Long id);
}
