package com.gerp.usermgmt.converter;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.IndividualScreen;
import com.gerp.usermgmt.model.Module;
import com.gerp.usermgmt.pojo.ModuleDto;
import com.gerp.usermgmt.repo.IndividualScreenRepo;
import com.gerp.usermgmt.repo.auth.PrivilegeRepo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ModuleConverter extends AbstractConverter<ModuleDto, Module> {

    private final CustomMessageSource customMessageSource;
    private final IndividualScreenRepo individualScreenRepo;
    private final PrivilegeRepo privilegeRepo;

    public ModuleConverter(CustomMessageSource customMessageSource, IndividualScreenRepo individualScreenRepo, PrivilegeRepo privilegeRepo) {
        this.customMessageSource = customMessageSource;
        this.individualScreenRepo = individualScreenRepo;
        this.privilegeRepo = privilegeRepo;
    }


    @Override
    public Module toEntity(ModuleDto dto) {
        Module entity = new Module();
        return toEntity(dto, entity);
    }

    @Override
    public Module toEntity(ModuleDto dto, Module entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setKey(dto.getKey());
        Optional<IndividualScreen> optionalIndividualScreen = individualScreenRepo.findById(dto.getScreenId());
        if (optionalIndividualScreen.isPresent())
            entity.setIndividualScreen(optionalIndividualScreen.get());
        else
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("screen")));
        entity.setPrivilegeList(dto.getPrivilegeList().stream().map(uuid -> privilegeRepo.findById(uuid).get()).collect(Collectors.toList()));
        return entity;
    }

    @Override
    public ModuleDto toDto(Module entity) {
        ModuleDto dto = new ModuleDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setKey(entity.getKey());
        dto.setScreenId(entity.getIndividualScreen().getId());
        dto.setScreenName(entity.getIndividualScreen().getName());
        dto.setScreenGroup(
                new IdNamePojo().builder()
                        .id(entity.getIndividualScreen().getScreenGroup().getId())
                        .name(entity.getIndividualScreen().getScreenGroup().getName())
                        .build()
        );
        dto.setPrivilegeList(entity.getPrivilegeList().stream().map(privilege -> privilege.getId()).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public List<Module> toEntity(List<ModuleDto> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<ModuleDto> toDto(List<Module> entityList) {
        return super.toDto(entityList);
    }
}
