package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.usermgmt.cache.UserMobilePrivilegeCacheRepo;
import com.gerp.usermgmt.cache.UserPrivilegeCacheRepo;
import com.gerp.usermgmt.mapper.usermgmt.RoleGroupMapper;
import com.gerp.usermgmt.mapper.usermgmt.RoleGroupScreenMappingMapper;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.model.Module;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.RoleGroupScreenMapping;
import com.gerp.usermgmt.pojo.*;
import com.gerp.usermgmt.pojo.auth.IndividualScreenRoleMappingPojo;
import com.gerp.usermgmt.pojo.auth.RoleGroupScreenMappingPojo;
import com.gerp.usermgmt.pojo.auth.ScreenGroupRoleMappingPojo;
import com.gerp.usermgmt.repo.*;
import com.gerp.usermgmt.services.usermgmt.RoleGroupScreenService;
import com.gerp.usermgmt.services.usermgmt.RoleGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class RoleGroupScreenServiceImpl implements RoleGroupScreenService {

    private final RoleGroupScreenMappingRepo roleGroupScreenMappingRepo;
    private final RoleGroupRepo roleGroupRepo;
    private final IndividualScreenRepo individualScreenRepo;
    private final RoleGroupScreenModulePrivilegeRepo roleGroupScreenModulePrivilegeRepo;
    private final RoleGroupScreenMappingMapper roleGroupScreenMappingMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private RoleGroupMapper roleGroupMapper;
    @Autowired private RoleGroupService roleGroupService;
    @Autowired private CustomMessageSource customMessageSource;
    @Autowired private ModuleRepo moduleRepo;
    @Autowired private UserPrivilegeCacheRepo userPrivilegeCacheRepo;
    @Autowired private UserMobilePrivilegeCacheRepo userMobilePrivilegeCacheRepo;

    public RoleGroupScreenServiceImpl(RoleGroupScreenMappingRepo roleGroupScreenMappingRepo,
                                      RoleGroupRepo roleGroupRepo,
                                      IndividualScreenRepo individualScreenRepo,
                                      RoleGroupScreenMappingMapper roleGroupScreenMappingMapper,
                                      RoleGroupScreenModulePrivilegeRepo roleGroupScreenModulePrivilegeRepo) {
        this.roleGroupScreenMappingRepo = roleGroupScreenMappingRepo;
        this.roleGroupRepo = roleGroupRepo;
        this.individualScreenRepo = individualScreenRepo;
        this.roleGroupScreenMappingMapper = roleGroupScreenMappingMapper;
        this.roleGroupScreenModulePrivilegeRepo = roleGroupScreenModulePrivilegeRepo;
    }

    @Override
    public void createRoleGroupScreenMapping(RoleGroupScreenMappingPojo roleGroupScreenMappingPojo) {
        Optional<RoleGroup> roleGroupOptional = roleGroupRepo.findById(roleGroupScreenMappingPojo.getRoleGroupId());
        RoleGroup roleGroup = null;
        if(roleGroupOptional.isPresent()) {
            roleGroupService.validateRoleGroupUpdate(roleGroupOptional.get());
            roleGroup = roleGroupOptional.get();
        }
        else
            throw new RuntimeException(customMessageSource.get("invalid.action"));

        RoleGroup finalRoleGroup = roleGroup;
        List<RoleGroupScreenMapping> roleGroupScreenMappings = roleGroupScreenMappingPojo.getIndividualScreenIds()
                .stream().map(x->{
                        return new RoleGroupScreenMapping().builder()
                                    .roleGroup(finalRoleGroup)
                                    .individualScreen(individualScreenRepo.findById(x).get())
                                    .build();
                }
                ).collect(Collectors.toList());
        roleGroupScreenMappingRepo.saveAll(roleGroupScreenMappings);
//        this.inactiveUnchecked(roleGroupScreenMappingPojo);
    }

//    private void inactiveUnchecked(RoleGroupScreenMappingPojo roleGroupScreenMappingPojo) {
//        List<Long> screenIds = roleGroupScreenMappingMapper.findScreenIds(roleGroupScreenMappingPojo.getScreenGroupId(), roleGroupScreenMappingPojo.getIndividualScreenIds());
//        roleGroupScreenMappingRepo.inactiveUnchecked(roleGroupScreenMappingPojo.getRoleGroupId(), screenIds);
//    }

    @Override
    public List<RoleGroupScreenMappingDto> findByRoleGroupId(Long roleGroupId) {
        return roleGroupScreenMappingRepo.findByRoleGroupId(roleGroupId);
    }

    @Override
    public List<RoleGroupScreenMappingDto> findByRoleGroupIdAndScreenGroupId(Long roleGroupId, Long screeGroupId) {
        return roleGroupScreenMappingMapper.findByRoleGroupIdAndScreenGroupId(roleGroupId, screeGroupId);
    }

    @Override
    public List<ModuleRoleMappingPojo> findByRoleGroupIdAndScreenIdOld(Long roleGroupId, Long screeId) {
        List<Module> moduleList = moduleRepo.findModuleListByIndividualScreen(screeId);
        List<ModuleRoleMappingPojo> moduleRoleMappingPojoList =
                moduleList.stream().map(
                        x-> new ModuleRoleMappingPojo().builder()
                                .id(x.getId())
                                .name(x.getName())
                                .privilegeList(
                                        x.getPrivilegeList().stream().map(
                                                y-> {
                                                    boolean status = roleGroupScreenModulePrivilegeRepo.findByRoleGroupIdAndScreenIdAndModuleIdAndPrivilageId(
                                                            roleGroupId, screeId, x.getId(), y.getId()
                                                    ) == 0 ? false : true;
                                                    return new PrivilegeRoleMappingPojo().builder()
                                                            .id(y.getId())
                                                            .name(y.getName())
                                                            .checked(status)
                                                            .build();
                                                }
                                        ).collect(Collectors.toList())
                                )
                                .build()
                ).collect(Collectors.toList());
        return moduleRoleMappingPojoList;
    }

    @Override
    public ModuleRoleMappingWithStatusPojo findByRoleGroupIdAndScreenId(Long roleGroupId, Long screeId) {
        List<Module> moduleList = moduleRepo.findModuleListByIndividualScreen(screeId);
        AtomicBoolean allCheckedStatus = new AtomicBoolean(true);
        List<ModuleRoleMappingPojo> moduleRoleMappingPojoList =
                moduleList.stream().map(
                        x-> new ModuleRoleMappingPojo().builder()
                                .id(x.getId())
                                .name(x.getName())
                                .privilegeList(
                                        x.getPrivilegeList().stream().map(
                                                y-> {
                                                    boolean status = roleGroupScreenModulePrivilegeRepo.findByRoleGroupIdAndScreenIdAndModuleIdAndPrivilageId(
                                                            roleGroupId, screeId, x.getId(), y.getId()
                                                    ) == 0 ? false : true;
                                                    if(allCheckedStatus.get())
                                                        allCheckedStatus.set(status && allCheckedStatus.get());
                                                    return new PrivilegeRoleMappingPojo().builder()
                                                            .id(y.getId())
                                                            .name(y.getName())
                                                            .checked(status)
                                                            .build();
                                                }
                                        ).collect(Collectors.toList())
                                )
                                .build()
                ).collect(Collectors.toList());
        return ModuleRoleMappingWithStatusPojo.builder()
                .isChecked(allCheckedStatus.get())
                .moduleRoleMappingPojoList(moduleRoleMappingPojoList)
                .build();

    }

    @Override
    public void deleteById(Long id) {
        Optional<RoleGroupScreenMapping> roleGroupScreenMappingOptional = roleGroupScreenMappingRepo.findById(id);
        if(!roleGroupScreenMappingOptional.isPresent())
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist",customMessageSource.get("mapping")));
        RoleGroupScreenMapping roleGroupScreenMapping = roleGroupScreenMappingOptional.get();

        roleGroupService.validateRoleGroupUpdate(roleGroupScreenMapping.getRoleGroup());

        Long roleGroupId = roleGroupScreenMapping.getRoleGroup().getId();
        Long screenId = roleGroupScreenMapping.getIndividualScreen().getId();
        String screeGroupKey = roleGroupScreenMapping.getIndividualScreen().getScreenGroup().getKey();
        String roleGroupKey = roleGroupScreenMapping.getRoleGroup().getKey();
        roleGroupScreenMappingRepo.delete(roleGroupScreenMapping);
        /**
         * We also need to remove the
         * role group screen module privilege related to this role and screen
         */
        roleGroupScreenModulePrivilegeRepo.deleteByRoleGroupIdAndScreenId(roleGroupId, screenId);

        // clear cache
        this.deleteByScreenGroupKeyAndRoleKey(
                screeGroupKey,
                roleGroupKey
        );
    }

    @Override
    public List<ScreenGroupRoleMappingPojo> getScreenGroups(Long roleGroupId) {
        roleGroupService.validateRoleGroupUpdate(roleGroupId);
        return roleGroupScreenMappingMapper.getScreenGroups(roleGroupId);
    }

    @Override
    public List<IndividualScreenRoleMappingPojo> findUnusedIndividualScreen(Long roleGroupId, Long screeGroupId) {
        return roleGroupScreenMappingMapper.findUnusedIndividualScreen(roleGroupId, screeGroupId);
    }

    private void deleteByScreenGroupKeyAndRoleKey(String screeGroupKey, String roleGroupKey) {
        Map<String, PrivilegeCacheDto> privilegeCacheDtoMap = userPrivilegeCacheRepo.findAll();
        for (Map.Entry<String, PrivilegeCacheDto> entry : privilegeCacheDtoMap.entrySet()) {
            String[] keyName = entry.getKey().split("_-_");
            if(keyName.length==2){
                if(keyName[0].equals(screeGroupKey))
                    if(keyName[1].contains(roleGroupKey))
                        userPrivilegeCacheRepo.deleteByKey(entry.getKey());
            }
        }

        Map<String, MobilePrivilegeCacheDto> privilegeCacheDtoMapM = userMobilePrivilegeCacheRepo.findAll();
        for (Map.Entry<String, MobilePrivilegeCacheDto> entry : privilegeCacheDtoMapM.entrySet()) {
            String[] keyName = entry.getKey().split("_-_");
            if(keyName.length==2){
                if(keyName[0].equals(screeGroupKey))
                    if(keyName[1].contains(roleGroupKey))
                        userMobilePrivilegeCacheRepo.deleteByKey(entry.getKey());
            }
        }
    }
}
