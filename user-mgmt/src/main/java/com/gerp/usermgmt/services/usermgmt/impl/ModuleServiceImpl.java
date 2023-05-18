package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.converter.ModuleConverter;
import com.gerp.usermgmt.enums.SystemUnchangeableRole;
import com.gerp.usermgmt.mapper.RoleGroupScreenModulePrivilegeMapper;
import com.gerp.usermgmt.model.IndividualScreen;
import com.gerp.usermgmt.model.Privilege;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.RoleGroupScreenModulePrivilege;
import com.gerp.usermgmt.pojo.ModuleDto;
import com.gerp.usermgmt.pojo.ModuleResponsePOJO;
import com.gerp.usermgmt.pojo.ScreenModulesDto;
import com.gerp.usermgmt.pojo.external.TMSScreenModelPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.repo.IndividualScreenRepo;
import com.gerp.usermgmt.repo.ModuleRepo;
import com.gerp.usermgmt.repo.RoleGroupRepo;
import com.gerp.usermgmt.repo.RoleGroupScreenModulePrivilegeRepo;
import com.gerp.usermgmt.services.usermgmt.ModuleService;
import org.springframework.stereotype.Service;
import com.gerp.usermgmt.model.Module;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepo moduleRepo;
    private final IndividualScreenRepo individualScreenRepo;
    private final ModuleConverter moduleConverter;
    private final CustomMessageSource customMessageSource;
    private final RoleGroupScreenModulePrivilegeRepo roleGroupScreenModulePrivilegeRepo;
    private final RoleGroupRepo roleGroupRepo;

    private final RoleGroupScreenModulePrivilegeMapper roleGroupScreenModulePrivilegeMapper;

    public ModuleServiceImpl(ModuleRepo moduleRepo,
                             IndividualScreenRepo individualScreenRepo,
                             ModuleConverter moduleConverter,
                             CustomMessageSource customMessageSource,
                             RoleGroupScreenModulePrivilegeRepo roleGroupScreenModulePrivilegeRepo,
                             RoleGroupRepo roleGroupRepo,
                             RoleGroupScreenModulePrivilegeMapper roleGroupScreenModulePrivilegeMapper) {
        this.moduleRepo = moduleRepo;
        this.individualScreenRepo = individualScreenRepo;
        this.moduleConverter = moduleConverter;
        this.customMessageSource = customMessageSource;
        this.roleGroupScreenModulePrivilegeRepo = roleGroupScreenModulePrivilegeRepo;
        this.roleGroupRepo = roleGroupRepo;
        this.roleGroupScreenModulePrivilegeMapper = roleGroupScreenModulePrivilegeMapper;
    }

    @Override
    public List<ModuleDto> findByScreen(Long screenId) {
        return moduleConverter.toDto(moduleRepo.findModuleListByIndividualScreen(screenId));
    }

    @Override
    public List<ScreenModulesDto> screensModuleList() {
        List<ScreenModulesDto> screenModulesDtoList = new ArrayList<>();
        List<IndividualScreen> individualScreens = individualScreenRepo.findAll();
        for (IndividualScreen individualScreen : individualScreens) {
            ScreenModulesDto screenModulesDto = new ScreenModulesDto();
            screenModulesDto.setScreenId(individualScreen.getId());
            screenModulesDto.setScreenName(individualScreen.getName());
            List<Module> moduleList = moduleRepo.findModuleListByIndividualScreen(individualScreen.getId());
            List<ModuleDto> moduleDtoList = moduleList.stream().map(module ->
                    new ModuleDto(module.getId(), module.getName(), individualScreen.getId(), individualScreen.getName(),
//                            new IdNamePojo().builder()
//                                    .id(individualScreen.getScreenGroup().getId())
//                                    .name(individualScreen.getScreenGroup().getName())
//                                    .build()
//                            ,
                            module.getPrivilegeList().stream().map(m -> m.getId()).collect(Collectors.toList()))).collect(Collectors.toList());
            screenModulesDto.setModuleDtoList(moduleDtoList);
            screenModulesDtoList.add(screenModulesDto);
        }
        return screenModulesDtoList;
    }

    @Override
    public ModuleDto createModule(ModuleDto moduleDto) {
        Module module = moduleConverter.toEntity(moduleDto);
        module = moduleRepo.save(module);
        /**
         * Update screen role privilege for super administrator
         */
        createRoleGroupScreenPrivilegeForSystemAdministrator(module);
        return moduleConverter.toDto(module);
    }

    @Override
    public ModuleDto findModuleById(Long moduleId) {
        Optional<Module> optionalModule = moduleRepo.findById(moduleId);
        if (optionalModule.isPresent())
            return moduleConverter.toDto(optionalModule.get());
        throw new RuntimeException(customMessageSource.get("id.notfound", customMessageSource.get("module")));
    }

    @Override
    public ModuleResponsePOJO findModuleByModuleId(Long moduleId) {
        Optional<Module> optionalModule = moduleRepo.findById(moduleId);
        if (optionalModule.isPresent()) {
            Module module = optionalModule.get();
            return ModuleResponsePOJO.builder()
                    .id(module.getId())
                    .name(module.getName())
                    .individualScreen(IdNamePojo.builder()
                            .id(module.getIndividualScreen().getId())
                            .name(module.getIndividualScreen().getName()).build())
                    .privilegeList(module.getPrivilegeList().stream().map(privilege -> IdNamePojo.builder().id(privilege.getId()).name(privilege.getName()).build()).collect(Collectors.toList())).build();
        }
        throw new RuntimeException(customMessageSource.get("id.notfound", customMessageSource.get("module")));
    }

    @Override
    public List<ModuleDto> findAllModule() {
        return moduleConverter.toDto(moduleRepo.findAll());
    }

    @Override
    public Module findModuleByModuleKey(String moduleKey) {
        Optional<Module> optionalModule = moduleRepo.findByKey(moduleKey);
        if (optionalModule.isPresent()) {
            return optionalModule.get();
        }
        return null;
    }

    private void createRoleGroupScreenPrivilegeForSystemAdministrator(Module module) {
        RoleGroup systemAdministratorRoleGroup = roleGroupRepo.findByKey(SystemUnchangeableRole.SUPER_ADMIN.toString()).get();
        Long individualScreenId = module.getIndividualScreen().getId();
        Long moduleId = module.getId();
        for (Privilege privilege : module.getPrivilegeList()) {
            RoleGroupScreenModulePrivilege check = roleGroupScreenModulePrivilegeRepo.
                    findByRoleGroupScreenModulePrivilege(systemAdministratorRoleGroup.getId(),
                            individualScreenId, moduleId, privilege.getId());
            if (check == null) {
                RoleGroupScreenModulePrivilege rmp = new RoleGroupScreenModulePrivilege();
                rmp.setRoleGroup(systemAdministratorRoleGroup);
                rmp.setIndividualScreen(module.getIndividualScreen());
                rmp.setModule(module);
                rmp.setPrivilege(privilege);
                roleGroupScreenModulePrivilegeRepo.save(rmp);
            }
        }
    }

    @Override
    public List<OfficePojo> getEmployeeListDetailByOffice(String officeCode) {
        return null;
    }

    @Override
    public Map<Long,Set<TMSScreenModelPojo>> getModulesByLoggedInUser(Map<Long, List<RoleGroup>> useridRoleMap) {
        Map<Long, Set<TMSScreenModelPojo>> response= new HashMap<>();
        useridRoleMap.keySet().forEach(
                userid-> {
                    Set<TMSScreenModelPojo> tmsScreenModelPojos = new HashSet<>();
                    tmsScreenModelPojos = this.getAllScreenModelPojo(useridRoleMap.get(userid));
                    response.put(userid, tmsScreenModelPojos);
                }
        );
        return response;
    }
    private Set<TMSScreenModelPojo> getAllScreenModelPojo(List<RoleGroup> roleGroups){
        Set<TMSScreenModelPojo> result = new HashSet<>();
        roleGroups.forEach(roleGroup -> {
            Set<TMSScreenModelPojo> tmsScreenModelPojos = roleGroupScreenModulePrivilegeMapper.findAllTMSScreenPOjoByRoleId(roleGroup.getId());
            result.addAll(tmsScreenModelPojos);
        });

        return result;
    }

    @Override
    public List<TMSScreenModelPojo> getAllModuleScreen() {
        List<ModuleDto> moduleDtos =  this.findAllModule();
        List<TMSScreenModelPojo> tmsScreenModelPojos = new ArrayList<>();
        moduleDtos.stream().forEach(moduleDto -> {
            TMSScreenModelPojo tmsScreenModelPojo = new TMSScreenModelPojo();
            tmsScreenModelPojo.setCode(moduleDto.getKey());
            tmsScreenModelPojo.setName(moduleDto.getName());
            tmsScreenModelPojo.setSource("productprocess");
            tmsScreenModelPojo.setId(moduleDto.getId());

            tmsScreenModelPojos.add(tmsScreenModelPojo);
        });

        return tmsScreenModelPojos;
    }

    @Override
    public TMSScreenModelPojo getModuleDetailsById(Long moduleId) {
        ModuleDto moduleDto = this.findModuleById(moduleId);
        return new TMSScreenModelPojo(moduleDto.getId(), "productprocess", moduleDto.getKey(), moduleDto.getName());
    }

    @Override
    public List<TMSScreenModelPojo> getAllModuleByIds(List<Long> moduleIds) {
        List<TMSScreenModelPojo> tmsScreenModelPojos = moduleIds.stream().map(moduleId -> {
            TMSScreenModelPojo tmsScreenModelPojo = this.getModuleDetailsById(moduleId);
            return tmsScreenModelPojo;
        }).collect(Collectors.toList());
        return tmsScreenModelPojos;
    }
}
