package com.gerp.usermgmt.converter;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.IndividualScreen;
import com.gerp.usermgmt.model.ScreenGroup;
import com.gerp.usermgmt.pojo.IndividualScreenDto;
import com.gerp.usermgmt.repo.ScreenGroupRepo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IndividualScreenConverter extends AbstractConverter<IndividualScreenDto, IndividualScreen> {

    private final ScreenGroupRepo screenGroupRepo;
    private final CustomMessageSource customMessageSource;

    public IndividualScreenConverter(ScreenGroupRepo screenGroupRepo, CustomMessageSource customMessageSource) {
        this.screenGroupRepo = screenGroupRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public IndividualScreen toEntity(IndividualScreenDto dto) {
        IndividualScreen entity = new IndividualScreen();
        return toEntity(dto, entity);
    }

    @Override
    public IndividualScreen toEntity(IndividualScreenDto dto, IndividualScreen entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setKey(dto.getKey());
        Optional<ScreenGroup> optionalScreenGroup = screenGroupRepo.findById(dto.getScreenGroupId());
        if (optionalScreenGroup.isPresent())
            entity.setScreenGroup(optionalScreenGroup.get());
        else
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("screengroup")));

        return entity;
    }

    @Override
    public IndividualScreenDto toDto(IndividualScreen entity) {
        IndividualScreenDto dto = new IndividualScreenDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setKey(entity.getKey());
        dto.setScreenGroupId(entity.getScreenGroup().getId());
        dto.setScreenGroupName(entity.getScreenGroup().getName());
        return dto;
    }

    @Override
    public List<IndividualScreen> toEntity(List<IndividualScreenDto> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<IndividualScreenDto> toDto(List<IndividualScreen> entityList) {
        return super.toDto(entityList);
    }
}
