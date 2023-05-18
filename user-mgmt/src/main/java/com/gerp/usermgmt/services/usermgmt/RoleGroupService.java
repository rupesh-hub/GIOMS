package com.gerp.usermgmt.services.usermgmt;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.pojo.auth.RolePojo;

import java.util.List;

public interface RoleGroupService extends GenericService<RoleGroup, Long> {
    RoleGroup findRoleGroupByKey(String roleGroupName);

    List<Long> findRoleGroupIdsByUsername(String username);

    List<RoleGroup> findRoleGroupListByUsername(String username);

    void validateRoleGroupUpdate(Long roleGroupId);
    void validateRoleGroupUpdate(RoleGroup update);

    boolean checkExistingRoleGroupMapping(String pisCode, String roleKey);

    void unAssignRole(String pisCode, String roleKey);

    void assignRole(String pisCode, String roleKey);

    List<RolePojo> getRolesMinimalDetail();
}
