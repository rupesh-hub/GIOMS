package com.gerp.usermgmt.services.usermgmt;


import com.gerp.usermgmt.pojo.ModuleRoleMappingPojo;
import com.gerp.usermgmt.pojo.ModuleRoleMappingWithStatusPojo;
import com.gerp.usermgmt.pojo.RoleGroupScreenMappingDto;
import com.gerp.usermgmt.pojo.auth.IndividualScreenRoleMappingPojo;
import com.gerp.usermgmt.pojo.auth.RoleGroupScreenMappingPojo;
import com.gerp.usermgmt.pojo.auth.ScreenGroupRoleMappingPojo;

import java.util.List;

public interface RoleGroupScreenService {

    void createRoleGroupScreenMapping(RoleGroupScreenMappingPojo roleGroupScreenMappingPojo);

    List<RoleGroupScreenMappingDto> findByRoleGroupId(Long roleGroupId);

    List<RoleGroupScreenMappingDto> findByRoleGroupIdAndScreenGroupId(Long roleGroupId, Long screeGroupId);

    List<ScreenGroupRoleMappingPojo> getScreenGroups(Long roleGroupId);

    List<IndividualScreenRoleMappingPojo> findUnusedIndividualScreen(Long roleGroupId, Long screeGroupId);

    void deleteById(Long id);

    ModuleRoleMappingWithStatusPojo findByRoleGroupIdAndScreenId(Long roleGroupId, Long screeId);

    List<ModuleRoleMappingPojo> findByRoleGroupIdAndScreenIdOld(Long roleGroupId, Long screeId);
}
