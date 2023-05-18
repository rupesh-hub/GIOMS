package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.cache.UserMobilePrivilegeCacheRepo;
import com.gerp.usermgmt.cache.UserPrivilegeCacheRepo;
import com.gerp.usermgmt.model.Module;
import com.gerp.usermgmt.model.*;
import com.gerp.usermgmt.pojo.*;
import com.gerp.usermgmt.pojo.auth.MobileScreenPrivilegePojo;
import com.gerp.usermgmt.pojo.auth.MobileSubModulePojo;
import com.gerp.usermgmt.repo.*;
import com.gerp.usermgmt.repo.auth.PrivilegeRepo;
import com.gerp.usermgmt.services.usermgmt.PermissionManagementService;
import com.gerp.usermgmt.services.usermgmt.RoleGroupService;
import com.gerp.usermgmt.services.usermgmt.ScreenGroupService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class PermissionManagementServiceImpl implements PermissionManagementService {
    private final IndividualScreenRepo individualScreenRepo;
    private final ModuleRepo moduleRepo;
    private final PrivilegeRepo privilegeRepo;
    private final RoleGroupScreenModulePrivilegeRepo roleGroupScreenModulePrivilegeRepo;
    private final RoleGroupRepo roleGroupRepo;
    private final UserPrivilegeCacheRepo userPrivilegeCacheRepo;
    @Autowired private UserMobilePrivilegeCacheRepo userMobilePrivilegeCacheRepo;
    @Autowired
    private RoleGroupScreenMappingRepo roleGroupScreenMappingRepo;
    @Autowired
    private RoleGroupService roleGroupService;
    @Autowired
    private ScreenGroupService screenGroupService;
    @Autowired
    private CustomMessageSource customMessageSource;

    public PermissionManagementServiceImpl(IndividualScreenRepo individualScreenRepo,
                                           ModuleRepo moduleRepo,
                                           PrivilegeRepo privilegeRepo,
                                           RoleGroupScreenModulePrivilegeRepo roleGroupScreenModulePrivilegeRepo,
                                           RoleGroupRepo roleGroupRepo,
                                           UserPrivilegeCacheRepo userPrivilegeCacheRepo) {
        this.individualScreenRepo = individualScreenRepo;

        this.moduleRepo = moduleRepo;
        this.privilegeRepo = privilegeRepo;
        this.roleGroupScreenModulePrivilegeRepo = roleGroupScreenModulePrivilegeRepo;
        this.roleGroupRepo = roleGroupRepo;
        this.userPrivilegeCacheRepo = userPrivilegeCacheRepo;
    }

//    @Override
//    public String saveRolePermission(List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList) {
//        try {
//            /**
//             * Delete all the privileges assigned and add again
//             */
//            deleteByRoleGroupId(roleGroupScreenModulePrivilegeDtoList.get(0).getRoleGroupId());
////            deleteByRoleGroupId(roleGroupScreenModulePrivilegeDtoList.getRoleGroupId());
//            saveRoleGroupScreenModulePrivilege(roleGroupScreenModulePrivilegeDtoList);
//            return "ok";
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }


    @Override
    public void saveIndividualPermission(ModuleWiseIndividualPrivilegeSaveDto data) {
        Optional<RoleGroupScreenMapping> roleGroupScreenMappingOptional = roleGroupScreenMappingRepo.findById(data.getMappingId());
        if (!roleGroupScreenMappingOptional.isPresent())
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("mapping")));
        RoleGroupScreenMapping roleGroupScreenMapping = roleGroupScreenMappingOptional.get();

        roleGroupService.validateRoleGroupUpdate(roleGroupScreenMapping.getRoleGroup());

        data.setRoleGroupId(roleGroupScreenMapping.getRoleGroup().getId());
        data.setScreenId(roleGroupScreenMapping.getIndividualScreen().getId());
        if (data.getChecked()) {
            RoleGroupScreenModulePrivilege permission = new RoleGroupScreenModulePrivilege().builder()
                    .roleGroup(roleGroupScreenMapping.getRoleGroup())
                    .individualScreen(roleGroupScreenMapping.getIndividualScreen())
                    .module(new Module(data.getModuleId()))
                    .privilege(new Privilege(data.getPrivilegeId()))
                    .build();
            roleGroupScreenModulePrivilegeRepo.save(permission);
        } else {
            roleGroupScreenModulePrivilegeRepo.deleteByRoleGroupScreenModulePrivilege(
                    data.getRoleGroupId(),
                    data.getScreenId(),
                    data.getModuleId(),
                    data.getPrivilegeId()
            );
        }
        this.deleteByScreenGroupKeyAndRoleKey(
                roleGroupScreenMapping.getIndividualScreen().getScreenGroup().getKey(),
                roleGroupScreenMapping.getRoleGroup().getKey()
        );
    }

    @Override
    public void saveAllPermission(ModuleWiseIndividualPrivilegeSaveDto data) {
        Optional<RoleGroupScreenMapping> roleGroupScreenMappingOptional = roleGroupScreenMappingRepo.findById(data.getMappingId());
        if (!roleGroupScreenMappingOptional.isPresent())
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("mapping")));
        RoleGroupScreenMapping roleGroupScreenMapping = roleGroupScreenMappingOptional.get();

        roleGroupService.validateRoleGroupUpdate(roleGroupScreenMapping.getRoleGroup());

        data.setRoleGroupId(roleGroupScreenMapping.getRoleGroup().getId());
        data.setScreenId(roleGroupScreenMapping.getIndividualScreen().getId());
        Optional<Module> moduleOptional = moduleRepo.findById(data.getModuleId());
        if (!moduleOptional.isPresent())
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("module")));
        Module module = moduleOptional.get();

        if (data.getChecked()) {
            module.getPrivilegeList().forEach(
                    x -> {
                        RoleGroupScreenModulePrivilege permission = new RoleGroupScreenModulePrivilege().builder()
                                .roleGroup(roleGroupScreenMapping.getRoleGroup())
                                .individualScreen(roleGroupScreenMapping.getIndividualScreen())
                                .module(new Module(data.getModuleId()))
                                .privilege(x)
                                .build();
                        roleGroupScreenModulePrivilegeRepo.save(permission);
                    }
            );
        } else {
            roleGroupScreenModulePrivilegeRepo.deleteByRoleGroupScreenModule(
                    data.getRoleGroupId(),
                    data.getScreenId(),
                    data.getModuleId()
            );
        }
        this.deleteByScreenGroupKeyAndRoleKey(
                roleGroupScreenMapping.getIndividualScreen().getScreenGroup().getKey(),
                roleGroupScreenMapping.getRoleGroup().getKey()
        );
    }

    @Override
    public void saveAllPermissionAllModule(ModuleWiseIndividualPrivilegeSaveDto data) {
        Optional<RoleGroupScreenMapping> roleGroupScreenMappingOptional = roleGroupScreenMappingRepo.findById(data.getMappingId());
        if (!roleGroupScreenMappingOptional.isPresent())
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("mapping")));
        RoleGroupScreenMapping roleGroupScreenMapping = roleGroupScreenMappingOptional.get();

        roleGroupService.validateRoleGroupUpdate(roleGroupScreenMapping.getRoleGroup());

        data.setRoleGroupId(roleGroupScreenMapping.getRoleGroup().getId());
        data.setScreenId(roleGroupScreenMapping.getIndividualScreen().getId());
        List<Module> moduleList = moduleRepo.findModuleListByIndividualScreen(data.getScreenId());
        if (data.getChecked()) {
            moduleList.forEach(y -> {
                y.getPrivilegeList().forEach(
                        x -> {
                            RoleGroupScreenModulePrivilege permission = roleGroupScreenModulePrivilegeRepo.findByRoleGroupScreenModulePrivilege(
                                    data.getRoleGroupId(),
                                    data.getScreenId(),
                                    y.getId(),
                                    x.getId()
                            );
                            if (permission == null) {
                                permission = new RoleGroupScreenModulePrivilege().builder()
                                        .roleGroup(roleGroupScreenMapping.getRoleGroup())
                                        .individualScreen(roleGroupScreenMapping.getIndividualScreen())
                                        .module(y)
                                        .privilege(x)
                                        .build();
                                roleGroupScreenModulePrivilegeRepo.save(permission);
                            }
                        }
                );
            });
        } else {
            roleGroupScreenModulePrivilegeRepo.deleteByRoleGroupScreen(
                    data.getRoleGroupId(),
                    data.getScreenId()
            );
        }
        this.deleteByScreenGroupKeyAndRoleKey(
                roleGroupScreenMapping.getIndividualScreen().getScreenGroup().getKey(),
                roleGroupScreenMapping.getRoleGroup().getKey()
        );
    }

    @Override
    public String saveRolePermissionScreenWise(ModuleWisePrivilegeSaveDto moduleWisePrivilegeSaveDto) {
        try {
            /**
             * Delete all the privileges assigned under a role group on particular screen
             */
            Optional<RoleGroupScreenMapping> roleGroupScreenMappingOptional = roleGroupScreenMappingRepo.findById(moduleWisePrivilegeSaveDto.getMappingId());
            if (!roleGroupScreenMappingOptional.isPresent())
                throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("mapping")));
            RoleGroupScreenMapping roleGroupScreenMapping = roleGroupScreenMappingOptional.get();

            roleGroupService.validateRoleGroupUpdate(roleGroupScreenMapping.getRoleGroup());

            moduleWisePrivilegeSaveDto.setRoleGroupId(roleGroupScreenMapping.getRoleGroup().getId());
            moduleWisePrivilegeSaveDto.setRoleGroupName(roleGroupScreenMapping.getRoleGroup().getName());
            moduleWisePrivilegeSaveDto.setScreenId(roleGroupScreenMapping.getIndividualScreen().getId());

            deleteByRoleGroupIdAndScreenIds(moduleWisePrivilegeSaveDto.getRoleGroupId(),
                    moduleWisePrivilegeSaveDto.getRoleGroupId());
            saveRoleGroupScreenModulePrivilege(moduleWisePrivilegeSaveDto);

//            /**
//             * Here on successful save of this privilege
//             * we need to remove the redis cache which contains this role group
//             */
            Map<String, PrivilegeCacheDto> privilegeCacheDtoMap = userPrivilegeCacheRepo.findAll();
//            // using for-each loop for iteration over Map.entrySet()
            for (Map.Entry<String, PrivilegeCacheDto> entry : privilegeCacheDtoMap.entrySet()) {
                String keyName = entry.getKey();
                if (keyName.contains(moduleWisePrivilegeSaveDto.getRoleGroupName())) {
                    userPrivilegeCacheRepo.deleteByKey(keyName);
                }
            }
            Map<String, MobilePrivilegeCacheDto> privilegeCacheDtoMap2 = userMobilePrivilegeCacheRepo.findAll();
//            // using for-each loop for iteration over Map.entrySet()
            for (Map.Entry<String, MobilePrivilegeCacheDto> entry : privilegeCacheDtoMap2.entrySet()) {
                String keyName = entry.getKey();
                if (keyName.contains(moduleWisePrivilegeSaveDto.getRoleGroupName())) {
                    userMobilePrivilegeCacheRepo.deleteByKey(keyName);
                }
            }
            return "ok";
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void saveRoleGroupScreenModulePrivilege(ModuleWisePrivilegeSaveDto moduleWisePrivilegeSaveDto) {
        Long roleGroupId = moduleWisePrivilegeSaveDto.getRoleGroupId();
        Long screenId = moduleWisePrivilegeSaveDto.getScreenId();
        RoleGroup roleGroup = roleGroupRepo.findById(roleGroupId).get();
        IndividualScreen individualScreen = individualScreenRepo.findById(screenId).get();
        for (RoleGroupScreenModulePrivilegeDto roleGroupScreenModulePrivilegeDto : moduleWisePrivilegeSaveDto.getRoleGroupScreenModulePrivilegeDtoList()) {
            Long moduleId = roleGroupScreenModulePrivilegeDto.getModuleId();
            Long[] privilegeIds = roleGroupScreenModulePrivilegeDto.getPrivilegeList();
            //get all individual data
            Module module = moduleRepo.findById(moduleId).get();
            // completed
            for (Long privilegeId : privilegeIds) {
                Privilege privilege = privilegeRepo.findById(privilegeId).get();
                RoleGroupScreenModulePrivilege permission = new RoleGroupScreenModulePrivilege();
                permission.setRoleGroup(roleGroup);
                permission.setIndividualScreen(individualScreen);
                permission.setModule(module);
                permission.setPrivilege(privilege);
                roleGroupScreenModulePrivilegeRepo.save(permission);
            }
        }
    }

    @Override
    public List<RoleGroupScreenModulePrivilegeDto> findRoleGroupScreenModulePrivilegeDtoListByRoleGroupId(Long roleGroupId) {
        List<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivileges = roleGroupScreenModulePrivilegeRepo.findByRoleGroup(roleGroupId);
        return prepareRoleGroupScreenModulePrivilegeDto(roleGroupScreenModulePrivileges);
    }

    @Override
    public List<RoleGroupScreenModulePrivilegeDto> findRoleGroupScreenModulePrivilegeDtoListByRoleGroupIdAndScreenId(Long roleGroupId, Long screenId) {
        List<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivileges =
                roleGroupScreenModulePrivilegeRepo.findByRoleGroupAndScreenId(roleGroupId, screenId);
        return prepareRoleGroupScreenModulePrivilegeDto(roleGroupScreenModulePrivileges);
    }

    @Override
    public void deleteByRoleGroupId(Long roleGroupId) {
        roleGroupScreenModulePrivilegeRepo.deleteByRoleGroupId(roleGroupId);
    }

    @Override
    public void deleteByRoleGroupIdAndScreenIds(Long roleGroupId, Long screenId) {
        roleGroupScreenModulePrivilegeRepo.deleteByRoleGroupIdAndScreenId(roleGroupId, screenId);
    }

    private boolean checkIfAlreadyExist(RoleGroupScreenModulePrivilege roleGroupScreenModulePrivilege,
                                        List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList) {
        for (RoleGroupScreenModulePrivilegeDto dto : roleGroupScreenModulePrivilegeDtoList) {
            if (roleGroupScreenModulePrivilege.getModule().getId().equals(dto.getModuleId())
                    &&
                    roleGroupScreenModulePrivilege.getIndividualScreen().getId().equals(dto.getScreenId())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Map<String, Map<String, Map<String, Object>>> screenWisePrivileges(List<Long> roleGroupIdList) {
        List<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivilegeList = roleGroupScreenModulePrivilegeRepo.findByRoleGroupIn(roleGroupIdList);
        /**
         * creating the screen key
         * initially find the active screens
         */
        Set<IndividualScreen> activeScreens = getActiveScreens(roleGroupScreenModulePrivilegeList);
        /**
         * Iterate the active screens
         * find the modules under every screen
         * find the privilege under every screen and
         * put all information into screenWise Map
         * and load all these to finalData as a response
         */
        Map<String, Map<String, Map<String, Object>>> finalData = new HashMap<>();
        for (IndividualScreen screen : activeScreens) {
            Map<String, Map<String, Map<String, Object>>> screenWise = new HashMap<>();
            /**
             * Find the roleGroupScreenModulePrivilegeList on the basis of screen only
             */
            List<RoleGroupScreenModulePrivilege> currentScreenRelated = roleGroupScreenModulePrivilegeList.stream().
                    filter(roleGroupScreenModulePrivilege ->
                            roleGroupScreenModulePrivilege.getIndividualScreen().getName().equals(screen.getName())).collect(Collectors.toList());
            Set<Module> activeModuleList = new HashSet<>();
            for (RoleGroupScreenModulePrivilege roleGroupScreenModulePrivilege : currentScreenRelated) {
                activeModuleList.add(roleGroupScreenModulePrivilege.getModule());
            }
            Map<String, Map<String, Object>> hashMap = new HashMap<>();
            for (Module module : activeModuleList) {
                List<RoleGroupScreenModulePrivilege> screenAndModuleRelated = roleGroupScreenModulePrivilegeList.stream().
                        filter(roleGroupScreenModulePrivilege ->
                                roleGroupScreenModulePrivilege.getIndividualScreen().getName().equals(screen.getName()) &&
                                        roleGroupScreenModulePrivilege.getModule().getName().equals(module.getName())).collect(Collectors.toList());
                Set<String> assignedPrivilegeSet = new HashSet<>();
                for (RoleGroupScreenModulePrivilege rmp : screenAndModuleRelated) {
                    assignedPrivilegeSet.add(rmp.getPrivilege().getName());
                }
                hashMap.put(module.getName(), getAvailablePrivilege(assignedPrivilegeSet, module));
            }
            screenWise.put(screen.getName(), hashMap);
            finalData.putAll(screenWise);
        }
        return finalData;
    }

    @Override
    public Map<String,Map<String, Map<String, Map<String, Object>>>> screeGroupWisePrivileges(String keyName, List<Long> roleGroupIdList) {

        Map<String,Map<String, Map<String, Map<String, Object>>>> finalData = new HashMap<>();

        if(roleGroupIdList.isEmpty())
            return null;

        List<ScreenGroup> screenGroups = screenGroupService.getAll();

        screenGroups.forEach(x->{
            String screenGroupKey = new StringBuilder().append(x.getKey()).append("_-_").append(keyName).toString();
            PrivilegeCacheDto privilegeCacheDto = userPrivilegeCacheRepo.findByKey(screenGroupKey);
            if (privilegeCacheDto == null) {
                log.info("---------------Cache not set Yet--------------------", screenGroupKey);
                List<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivilegeList
                        = roleGroupScreenModulePrivilegeRepo.findByRoleGroupIdsAndScreenGroupId(roleGroupIdList,x.getId());
                Set<IndividualScreen> activeScreens = getActiveScreens(roleGroupScreenModulePrivilegeList);
                Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
                for (IndividualScreen screen : activeScreens) {
                    Map<String, Map<String, Map<String, Object>>> screenWise = new HashMap<>();
                    List<RoleGroupScreenModulePrivilege> currentScreenRelated = roleGroupScreenModulePrivilegeList.stream().
                            filter(roleGroupScreenModulePrivilege ->
                                    roleGroupScreenModulePrivilege.getIndividualScreen().getKey().equals(screen.getKey())).collect(Collectors.toList());
                    Set<Module> activeModuleList = new HashSet<>();
                    for (RoleGroupScreenModulePrivilege roleGroupScreenModulePrivilege : currentScreenRelated) {
                        activeModuleList.add(roleGroupScreenModulePrivilege.getModule());
                    }
                    Map<String, Map<String, Object>> hashMap = new HashMap<>();
                    for (Module module : activeModuleList) {
                        List<RoleGroupScreenModulePrivilege> screenAndModuleRelated = roleGroupScreenModulePrivilegeList.stream().
                                filter(roleGroupScreenModulePrivilege ->
                                        roleGroupScreenModulePrivilege.getIndividualScreen().getKey().equals(screen.getKey()) &&
                                                roleGroupScreenModulePrivilege.getModule().getKey().equals(module.getKey())).collect(Collectors.toList());
                        Set<String> assignedPrivilegeSet = new HashSet<>();
                        for (RoleGroupScreenModulePrivilege rmp : screenAndModuleRelated) {
                            assignedPrivilegeSet.add(rmp.getPrivilege().getKey());
                        }
                        if(assignedPrivilegeSet.contains(StringConstants.READ_PRIVILEGE))
                            hashMap.put(module.getKey(), getAvailablePrivilege(assignedPrivilegeSet, module));
                    }
                    screenWise.put(screen.getKey(), hashMap);
                    data.putAll(screenWise);
                }
                privilegeCacheDto = new PrivilegeCacheDto().builder().key(screenGroupKey).value(data).build();
//                userPrivilegeCacheRepo.saveScreenWisePrivilege(privilegeCacheDto);
                finalData.put(x.getKey(),data);
            }else{
                log.info("---------------Cache already set--------------------");
                finalData.put(x.getKey(),privilegeCacheDto.getValue());
            }

        });
        return finalData;
    }

    @Override
    public List<MobileScreenPrivilegePojo> screeGroupWiseMobilePrivileges(String keyName, List<Long> roleGroupIdList) {

        List<MobileScreenPrivilegePojo> finalData = new ArrayList<>();

        if(roleGroupIdList.isEmpty())
            return null;

        List<ScreenGroup> screenGroups = screenGroupService.getAll();

        screenGroups.forEach(x->{
            String screenGroupKey = new StringBuilder().append(x.getKey()).append("_-_").append(keyName).toString();
            MobilePrivilegeCacheDto mobilePrivilegeCacheDto = userMobilePrivilegeCacheRepo.findByKey(screenGroupKey);
            if (mobilePrivilegeCacheDto == null) {
                log.info("---------------Cache not set Yet M --------------------", screenGroupKey);
                List<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivilegeList
                        = roleGroupScreenModulePrivilegeRepo.findByRoleGroupIdsAndScreenGroupId(roleGroupIdList,x.getId());
                Set<IndividualScreen> activeScreens = getActiveScreens(roleGroupScreenModulePrivilegeList);
                List<MobileScreenPrivilegePojo> subMenus = new ArrayList<>();
                for (IndividualScreen screen : activeScreens) {
                    Map<String, Map<String, Map<String, Object>>> screenWise = new HashMap<>();
                    List<RoleGroupScreenModulePrivilege> currentScreenRelated = roleGroupScreenModulePrivilegeList.stream().
                            filter(roleGroupScreenModulePrivilege ->
                                    roleGroupScreenModulePrivilege.getIndividualScreen().getKey().equals(screen.getKey())).collect(Collectors.toList());
                    Set<Module> activeModuleList = new HashSet<>();
                    for (RoleGroupScreenModulePrivilege roleGroupScreenModulePrivilege : currentScreenRelated) {
                        activeModuleList.add(roleGroupScreenModulePrivilege.getModule());
                    }
                    List<MobileSubModulePojo> subModuleList = new ArrayList<>();
                    for (Module module : activeModuleList) {
                        List<RoleGroupScreenModulePrivilege> screenAndModuleRelated = roleGroupScreenModulePrivilegeList.stream().
                                filter(roleGroupScreenModulePrivilege ->
                                        roleGroupScreenModulePrivilege.getIndividualScreen().getKey().equals(screen.getKey()) &&
                                                roleGroupScreenModulePrivilege.getModule().getKey().equals(module.getKey())).collect(Collectors.toList());
                        Set<String> assignedPrivilegeSet = new HashSet<>();
                        for (RoleGroupScreenModulePrivilege rmp : screenAndModuleRelated) {
                            assignedPrivilegeSet.add(rmp.getPrivilege().getKey());
                        }
                        if(assignedPrivilegeSet.contains(StringConstants.READ_PRIVILEGE)){
                            subModuleList.add(MobileSubModulePojo.builder()
                                    .key(module.getKey())
                                    .privileges(getAvailablePrivilege(assignedPrivilegeSet, module))
                                    .build());
                        }
                    }
                    if(!subModuleList.isEmpty())
                        subMenus.add(MobileScreenPrivilegePojo.builder()
                                .key(screen.getKey())
                                .subModules(subModuleList)
                                .build());
                }
                Type listType = new TypeToken<List<Map<String,Object>>>() {}.getType();
                Type listType2 = new TypeToken<List<MobileScreenPrivilegePojo>>() {}.getType();
                Gson gson = new Gson();
                String json = gson.toJson(subMenus, listType2);
                List<Map<String,Object>> output = gson.fromJson(json, listType);
                mobilePrivilegeCacheDto = new MobilePrivilegeCacheDto().builder().key(screenGroupKey).mobileValue(output).build();
                userMobilePrivilegeCacheRepo.saveScreenWisePrivilege(mobilePrivilegeCacheDto);
                if(!subMenus.isEmpty())
                    finalData.add(MobileScreenPrivilegePojo.builder()
                            .key(x.getKey())
                            .subMenus(subMenus)
                            .build());
            }else{
                log.info("---------------Cache already set M--------------------");
                Type listType = new TypeToken<List<Map<String,Object>>>() {}.getType();
                Type listType2 = new TypeToken<List<MobileScreenPrivilegePojo>>() {}.getType();
                List<Map<String,Object>> data = mobilePrivilegeCacheDto.getMobileValue();
                Gson gson = new Gson();
                String json = gson.toJson(data, listType);
                List<MobileScreenPrivilegePojo> output = gson.fromJson(json, listType2);
//                List<MobileScreenPrivilegePojo> output = mobilePrivilegeCacheDto.getMobileValue();
                finalData.add(MobileScreenPrivilegePojo.builder()
                        .key(x.getKey())
                        .subMenus(output)
                        .build());
            }

        });
        return finalData;
    }

    @Override
    public Set<String> modulePrivilegeList(List<Long> roleGroupIdList) {
        Set<String> grantedPrivilegeList = new HashSet<>();
        List<RoleGroupScreenModulePrivilege> rmpList = roleGroupScreenModulePrivilegeRepo.findByRoleGroupIn(roleGroupIdList);
        Set<IndividualScreen> activeScreens = getActiveScreens(rmpList);
        for (IndividualScreen activeScreen : activeScreens) {
            List<RoleGroupScreenModulePrivilege> listAccordingToScreen = rmpList.stream().filter(roleGroupScreenModulePrivilege ->
                    roleGroupScreenModulePrivilege.getIndividualScreen().getName().equals(activeScreen.getName())).collect(Collectors.toList());
            Set<Module> activeModuleList = new HashSet<>();
            for (RoleGroupScreenModulePrivilege roleGroupScreenModulePrivilege : listAccordingToScreen) {
                activeModuleList.add(roleGroupScreenModulePrivilege.getModule());
            }
            for (Module module : activeModuleList) {
                List<RoleGroupScreenModulePrivilege> screenAndModuleRelated = rmpList.stream().filter(roleGroupScreenModulePrivilege ->
                        roleGroupScreenModulePrivilege.getIndividualScreen().getName().equals(activeScreen.getName()) &&
                                roleGroupScreenModulePrivilege.getModule().getName().equals(module.getName())).collect(Collectors.toList());
                for (RoleGroupScreenModulePrivilege rmp : screenAndModuleRelated) {
                    String grantedPrivilege = (module.getName() + "_" + rmp.getPrivilege().getName()).toLowerCase();
                    grantedPrivilegeList.add(grantedPrivilege);
                }
            }
        }
        return grantedPrivilegeList;
    }

    @Override
    public List<User> findByGroupAndScreen(List<ModulePrivilageGroup> modulePrivilegeGroups) {
        List<User> users = new ArrayList<>();
        List<RoleGroupScreenModulePrivilege> modulePrivileges = new ArrayList<>();
        modulePrivilegeGroups.stream().
                forEach(modulePrivilegeGroup -> {
                    modulePrivileges.addAll(roleGroupScreenModulePrivilegeRepo.findByModuleAndPrivilege(modulePrivilegeGroup.getModuleId(), modulePrivilegeGroup.getPrivilegeId()));
                });
        for (RoleGroupScreenModulePrivilege roleGroupScreenModulePrivilege : modulePrivileges) {
            if (roleGroupScreenModulePrivilege.getRoleGroup() != null) {
                users.addAll(roleGroupScreenModulePrivilege.getRoleGroup().getUsers());
            }
        }
        return users;
    }

    private Map<String, Object> getAvailablePrivilege(Set<String> assignedPrivilegeList, Module module) {
        Map<String, Object> availablePrivilegeMap = new HashMap<>();
        List<Privilege> actualPrivilegeList = module.getPrivilegeList();
        for (Privilege privilege : actualPrivilegeList) {
            if (checkIfPrivilegeExists(privilege.getKey(), assignedPrivilegeList)) {
                availablePrivilegeMap.put(privilege.getKey(), 1);
            } else {
                availablePrivilegeMap.put(privilege.getKey(), 0);
            }
        }
        return availablePrivilegeMap;
    }


    private Set<IndividualScreen> getActiveScreens(List<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivilegeList) {
        Set<IndividualScreen> activeScreens = new HashSet<>();
        for (RoleGroupScreenModulePrivilege roleGroupScreenModulePrivilege : roleGroupScreenModulePrivilegeList) {
            activeScreens.add(roleGroupScreenModulePrivilege.getIndividualScreen());
        }
        return activeScreens;
    }

    private Boolean checkIfPrivilegeExists(String privilegeName, Set<String> assignedPrivilegeList) {
        return assignedPrivilegeList.stream().anyMatch(s -> s.equalsIgnoreCase(privilegeName));
    }

    /**
     * Prepare RoleGroupScreenModulePrivilegeDto List
     *
     * @param roleGroupScreenModulePrivileges
     * @return
     */
    private List<RoleGroupScreenModulePrivilegeDto> prepareRoleGroupScreenModulePrivilegeDto(List<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivileges) {
        List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList = new ArrayList<>();
        for (RoleGroupScreenModulePrivilege rmp : roleGroupScreenModulePrivileges) {
            IndividualScreen individualScreen = rmp.getIndividualScreen();
            Long screenId = individualScreen.getId();
            List<RoleGroupScreenModulePrivilege> screenWiseFilter =
                    roleGroupScreenModulePrivileges.stream().filter(roleGroupModulePrivilege1 ->
                            screenId.equals(roleGroupModulePrivilege1.getIndividualScreen().getId())).collect(Collectors.toList());
            for (RoleGroupScreenModulePrivilege screenWise : screenWiseFilter) {
                Long mId = screenWise.getModule().getId();
                List<RoleGroupScreenModulePrivilege> moduleWiseFilter =
                        screenWiseFilter.stream().filter(roleGroupModulePrivilege1 ->
                                mId.equals(roleGroupModulePrivilege1.getModule().getId())).collect(Collectors.toList());
                Long[] ids = new Long[moduleWiseFilter.size()];
                for (RoleGroupScreenModulePrivilege moduleWise : moduleWiseFilter) {
                    if (!checkIfAlreadyExist(moduleWise, roleGroupScreenModulePrivilegeDtoList)) {
                        RoleGroupScreenModulePrivilegeDto roleGroupScreenModulePrivilegeDto = new RoleGroupScreenModulePrivilegeDto();
                        //end save
                        Long mm = moduleWise.getModule().getId();
                        Long ss = moduleWise.getIndividualScreen().getId();
                        Long rr = moduleWise.getRoleGroup().getId();
                        roleGroupScreenModulePrivilegeDto.setRoleGroupId(rr);
                        roleGroupScreenModulePrivilegeDto.setScreenId(ss);
                        roleGroupScreenModulePrivilegeDto.setModuleId(mm);
                        for (int i = 0; i < ids.length; i++) {
                            ids[i] = moduleWiseFilter.get(i).getPrivilege().getId();
                        }
                        roleGroupScreenModulePrivilegeDto.setPrivilegeList(ids);
                        roleGroupScreenModulePrivilegeDtoList.add(roleGroupScreenModulePrivilegeDto);
                    }
                }
            }
        }
        return roleGroupScreenModulePrivilegeDtoList;
    }

    private Set<Module> removeUnwantedModules(Set<Module> activeModuleList, List<String> moduleList) {
        Set<Module> filteredModule = new HashSet<>();
        for (Module module : activeModuleList) {
            if (checkIfExists(module.getName(), moduleList))
                filteredModule.add(module);
        }
        return filteredModule;
    }

    private boolean checkIfExists(String moduleName, List<String> moduleList) {
        for (String string : moduleList) {
            if (moduleName.equalsIgnoreCase(string))
                return true;
        }
        return false;
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


























