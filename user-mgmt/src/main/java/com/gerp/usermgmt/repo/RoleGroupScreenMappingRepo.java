package com.gerp.usermgmt.repo;

import com.gerp.usermgmt.model.RoleGroupScreenMapping;
import com.gerp.usermgmt.pojo.RoleGroupScreenMappingDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleGroupScreenMappingRepo extends JpaRepository<RoleGroupScreenMapping, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from role_group_screen_mapping where role_group_id=?1 and individual_screen_id=?2")
    void deleteByRoleGroupAndIndividualScreen(Long roleGroupId, Long individualScreenId);

    @Query(nativeQuery = true, value = "select * from role_group_screen_mapping where role_group_id=?1 and individual_screen_id=?2")
    RoleGroupScreenMapping findByRoleGroupAndIndividualScreen(Long roleGroupId, Long individualScreenId);

    @Query("SELECT new com.gerp.usermgmt.pojo.RoleGroupScreenMappingDto(rg.id , rg.individualScreen.id , rg.individualScreen.name , rg.roleGroup.id , rg.roleGroup.name) from RoleGroupScreenMapping rg where rg.roleGroup.id=?1")
    List<RoleGroupScreenMappingDto> findByRoleGroupId(Long roleGroupId);

//    @Modifying
//    @Transactional
//    @Query(value = "update role_group_screen_mapping\n" +
//            "set status = false\n" +
//            "where role_group_id = ?1\n" +
//            "  and individual_screen_id in ?2 ", nativeQuery = true)
//    void inactiveUnchecked(Long roleGroupId, List<Long> screenIds);
}
