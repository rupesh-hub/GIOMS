package com.gerp.usermgmt.services.usermgmt;

import com.gerp.usermgmt.model.Module;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.pojo.ModuleDto;
import com.gerp.usermgmt.pojo.ModuleResponsePOJO;
import com.gerp.usermgmt.pojo.ScreenModulesDto;
import com.gerp.usermgmt.pojo.external.TMSScreenModelPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleService {

    List<ModuleDto> findByScreen(Long screenId);

    List<ScreenModulesDto> screensModuleList();

    ModuleDto createModule(ModuleDto moduleDto);

    ModuleDto findModuleById(Long moduleId);

    ModuleResponsePOJO findModuleByModuleId(Long moduleId);

    List<ModuleDto> findAllModule();

    Module findModuleByModuleKey(String moduleName);

    List<OfficePojo> getEmployeeListDetailByOffice(String officeCode);

    Map<Long, Set<TMSScreenModelPojo>> getModulesByLoggedInUser(Map<Long, List<RoleGroup>> userIdRoleMap);

    List<TMSScreenModelPojo> getAllModuleScreen();

    TMSScreenModelPojo getModuleDetailsById(Long moduleId);

    List<TMSScreenModelPojo> getAllModuleByIds(List<Long> moduleIds);
}
