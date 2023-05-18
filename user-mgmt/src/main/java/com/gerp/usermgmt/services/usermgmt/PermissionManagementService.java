package com.gerp.usermgmt.services.usermgmt;

import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.pojo.ModulePrivilageGroup;
import com.gerp.usermgmt.pojo.ModuleWiseIndividualPrivilegeSaveDto;
import com.gerp.usermgmt.pojo.ModuleWisePrivilegeSaveDto;
import com.gerp.usermgmt.pojo.RoleGroupScreenModulePrivilegeDto;
import com.gerp.usermgmt.pojo.auth.MobileScreenPrivilegePojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionManagementService {

//    String saveRolePermission(List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList);

    String saveRolePermissionScreenWise(ModuleWisePrivilegeSaveDto moduleWisePrivilegeSaveDto);

    List<RoleGroupScreenModulePrivilegeDto> findRoleGroupScreenModulePrivilegeDtoListByRoleGroupId(Long roleGroupId);

    List<RoleGroupScreenModulePrivilegeDto> findRoleGroupScreenModulePrivilegeDtoListByRoleGroupIdAndScreenId(Long roleGroupId, Long screenId);

    void deleteByRoleGroupId(Long roleGroupId);

    void deleteByRoleGroupIdAndScreenIds(Long roleGroupId, Long screenId);

    Map<String, Map<String, Map<String, Object>>> screenWisePrivileges(List<Long> roleGroupIdList);

    Map<String,Map<String, Map<String, Map<String, Object>>>> screeGroupWisePrivileges(String keyName, List<Long> roleGroupIdList);

    Set<String> modulePrivilegeList(List<Long> roleGroupIdList);

    List<User> findByGroupAndScreen(List<ModulePrivilageGroup> modulePrivilegeGroups);

    void saveIndividualPermission(ModuleWiseIndividualPrivilegeSaveDto moduleWiseIndividualPrivilegeSaveDto);

    void saveAllPermission(ModuleWiseIndividualPrivilegeSaveDto moduleWiseIndividualPrivilegeSaveDto);

    void saveAllPermissionAllModule(ModuleWiseIndividualPrivilegeSaveDto moduleWiseIndividualPrivilegeSaveDto);

    List<MobileScreenPrivilegePojo> screeGroupWiseMobilePrivileges(String keyNameMobile, List<Long> roleGroupIdList);
}
