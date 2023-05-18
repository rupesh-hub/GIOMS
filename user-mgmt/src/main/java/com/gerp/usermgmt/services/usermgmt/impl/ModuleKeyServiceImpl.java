package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.usermgmt.model.ModuleKey;
import com.gerp.usermgmt.pojo.ModuleKeyPojo;
import com.gerp.usermgmt.repo.ModuleKeyRepo;
import com.gerp.usermgmt.services.usermgmt.ModuleKeyService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModuleKeyServiceImpl implements ModuleKeyService {

    private final ModuleKeyRepo moduleKeyRepo;

    private final ModelMapper modelMapper;

    public ModuleKeyServiceImpl(ModuleKeyRepo moduleKeyRepo,
                                ModelMapper modelMapper) {
        this.moduleKeyRepo = moduleKeyRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ModuleKeyPojo> findAll() {
        List<ModuleKey> moduleKeys = moduleKeyRepo.findAll();

        return moduleKeys.stream().map(moduleKey -> {
            ModuleKeyPojo moduleKeyPojo = new ModuleKeyPojo();
            modelMapper.map(moduleKey, moduleKeyPojo);
            return moduleKeyPojo;
        }).collect(Collectors.toList());
    }
}
