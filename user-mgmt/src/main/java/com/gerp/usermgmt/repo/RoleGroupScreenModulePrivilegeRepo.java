package com.gerp.usermgmt.repo;

import com.gerp.usermgmt.model.IndividualScreen;
import com.gerp.usermgmt.model.RoleGroupScreenModulePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface RoleGroupScreenModulePrivilegeRepo extends JpaRepository<RoleGroupScreenModulePrivilege, Long> {


    @Query(value = "select * from role_group_screen_module_privilege where role_group_id=?1", nativeQuery = true)
    List<RoleGroupScreenModulePrivilege> findByRoleGroup(Long id);

    @Query(value = "select * from role_group_screen_module_privilege where role_group_id =?1 and screen_id=?2", nativeQuery = true)
    List<RoleGroupScreenModulePrivilege> findByRoleGroupAndScreenId(Long roleGroupId, Long screenId);

    @Modifying
    @Query(value = "delete from role_group_screen_module_privilege where role_group_id=?1", nativeQuery = true)
    void deleteByRoleGroupId(Long roleGroupId);

    @Modifying
    @Transactional
    @Query(value = "delete from role_group_screen_module_privilege where role_group_id=?1 and screen_id=?2 ", nativeQuery = true)
    void deleteByRoleGroupIdAndScreenId(Long roleGroupId, Long screenId);

    @Query(value = "select * from role_group_screen_module_privilege where role_group_id in ?1", nativeQuery = true)
    List<RoleGroupScreenModulePrivilege> findByRoleGroupIn(List<Long> uuidList);

    @Query(value = "select * from role_group_screen_module_privilege where role_group_id=?1 and screen_id=?2 and module_id=?3 and privilege_id=?4", nativeQuery = true)
    RoleGroupScreenModulePrivilege findByRoleGroupScreenModulePrivilege(Long roleGroupId, Long screenId, Long moduleId, Long privilegeId);

    @Query(value = "select * from role_group_screen_module_privilege where module_id = ?1 and privilege_id=?2", nativeQuery = true)
    List<RoleGroupScreenModulePrivilege> findByModuleAndPrivilege(Long module_id, Long screenId);

    @Query("select distinct i \n" +
            "from RoleGroupScreenModulePrivilege rgsmp\n" +
            "         inner join IndividualScreen i on rgsmp.individualScreen.id = i.id\n" +
            "where rgsmp.roleGroup.id in (?1)")
    Set<IndividualScreen> distinctScreenForTheRoleGroups(List<Long> roleGroupIdList);


    @Query(value = "select rgsmp.*\n" +
            "    from role_group_screen_module_privilege rgsmp\n" +
            "    inner join individual_screen i on rgsmp.screen_id = i.id\n" +
            "    where rgsmp.role_group_id in (?1) and screen_name=?2", nativeQuery = true)
    List<RoleGroupScreenModulePrivilege> distinctScreenForTheRoleGroupsAndScreenName(List<Long> roleGroupIdList, String screenName);

    @Query(value = "select count(*) \n" +
            "from role_group_screen_module_privilege\n" +
            "where role_group_id = ?1\n" +
            "  and screen_id = ?2\n" +
            "  and module_id = ?3\n" +
            "  and privilege_id =?4 ", nativeQuery = true)
    Long findByRoleGroupIdAndScreenIdAndModuleIdAndPrivilageId(Long roleGroupId, Long screeId, Long moduleId, Long privilegeId);

    @Modifying
    @Transactional
    @Query(value = "delete from role_group_screen_module_privilege \n"+
            "where role_group_id = ?1\n" +
            "  and screen_id = ?2\n" +
            "  and module_id = ?3\n" +
            "  and privilege_id =?4", nativeQuery = true)
    void deleteByRoleGroupScreenModulePrivilege(Long roleGroupId, Long screenId, Long moduleId, Long privilegeId);

    @Modifying
    @Transactional
    @Query(value = "delete from role_group_screen_module_privilege \n"+
            "where role_group_id = ?1\n" +
            "  and screen_id = ?2\n" +
            "  and module_id = ?3", nativeQuery = true)
    void deleteByRoleGroupScreenModule(Long roleGroupId, Long screenId, Long moduleId);

    @Modifying
    @Transactional
    @Query(value = "delete from role_group_screen_module_privilege \n"+
            "where role_group_id = ?1\n" +
            "  and screen_id = ?2 ", nativeQuery = true)
    void deleteByRoleGroupScreen(Long roleGroupId, Long screenId);

    @Query(value = "select rgsmp.*\n" +
            "from role_group_screen_module_privilege rgsmp\n" +
            "         inner join individual_screen s on rgsmp.screen_id = s.id\n" +
            "where role_group_id in ?1\n" +
            "  and s.screen_group_id = ?2 ", nativeQuery = true)
    List<RoleGroupScreenModulePrivilege> findByRoleGroupIdsAndScreenGroupId(List<Long> roleGroupIdList, Long id);
}
