package com.gerp.usermgmt.converter;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.ModuleApiMapping;
import com.gerp.usermgmt.pojo.auth.ModuleApiMappingPojo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModuleApiMappingConverter extends AbstractConverter<ModuleApiMappingPojo, ModuleApiMapping> {
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ModuleApiMapping toEntity(ModuleApiMappingPojo dto) {
        ModuleApiMapping entity = new ModuleApiMapping();
        modelMapper.map(dto , entity);
        return entity;
    }

    @Override
    public List<ModuleApiMapping> toEntity(List<ModuleApiMappingPojo> dtoList) {
        if (dtoList == null) {
            return null;
        }

        if (dtoList.isEmpty()) {
            return null;
        }

        return dtoList.parallelStream().map(this::toEntity).collect(Collectors.toList());
    }



    public List<ModuleApiMappingPojo> toJsonDto(List<ModuleApiMapping> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::toJsonDto).collect(Collectors.toList());
    }


    public ModuleApiMappingPojo toJsonDto(ModuleApiMapping entity) {
        return ModuleApiMappingPojo.builder().moduleId(entity.getModuleId()).
                method(entity.getMethod()).api(entity.getApi()).privilegeId(entity.getPrivilegeId()).build();

    }

}
