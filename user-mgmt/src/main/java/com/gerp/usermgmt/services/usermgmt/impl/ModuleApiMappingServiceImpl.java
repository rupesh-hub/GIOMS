package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.mapper.ModuleApiMappingMapper;
import com.gerp.usermgmt.model.ModuleApiMapping;
import com.gerp.usermgmt.pojo.auth.ModuleApiMappingPojo;
import com.gerp.usermgmt.repo.auth.ModuleApiMappingRepo;
import com.gerp.usermgmt.services.usermgmt.ModuleApiMappingService;
import com.gerp.usermgmt.util.ModuleApiDataUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModuleApiMappingServiceImpl extends GenericServiceImpl<ModuleApiMapping, Long> implements ModuleApiMappingService  {
    private final ModelMapper modelMapper;
    private final ModuleApiMappingRepo moduleApiMappingRepo;
    private final ModuleApiMappingMapper moduleApiMappingMapper;

    @Autowired
    private ModuleApiDataUtils moduleApiDataUtils;

    public ModuleApiMappingServiceImpl(ModelMapper modelMapper, ModuleApiMappingRepo moduleApiMappingRepo, ModuleApiMappingMapper moduleApiMappingMapper) {
        super(moduleApiMappingRepo);
        this.modelMapper = modelMapper;
        this.moduleApiMappingRepo = moduleApiMappingRepo;
        this.moduleApiMappingMapper = moduleApiMappingMapper;
    }

    @Override
    @Transactional
    public void create(List<ModuleApiMappingPojo> dto) {
        List<ModuleApiMapping>  newInstance = new ArrayList<>();
        modelMapper.map(dto,newInstance);
        moduleApiMappingRepo.saveAll(newInstance);
    }

    @Override
    public List<ModuleApiMappingPojo> findByModuleId(Long moduleId) {
        return moduleApiMappingMapper.findByModuleId(moduleId);
    }
}
