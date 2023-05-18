package com.gerp.usermgmt.services.usermgmt;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.ModuleApiMapping;
import com.gerp.usermgmt.pojo.auth.ModuleApiMappingPojo;

import java.util.List;

public interface ModuleApiMappingService extends GenericService<ModuleApiMapping, Long> {

     void create(List<ModuleApiMappingPojo> newInstance);

     List<ModuleApiMappingPojo> findByModuleId(Long moduleId);
}
