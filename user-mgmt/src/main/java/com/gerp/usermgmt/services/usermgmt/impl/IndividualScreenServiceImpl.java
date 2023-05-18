package com.gerp.usermgmt.services.usermgmt.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.usermgmt.converter.IndividualScreenConverter;
import com.gerp.usermgmt.model.IndividualScreen;
import com.gerp.usermgmt.pojo.IndividualScreenDto;
import com.gerp.usermgmt.repo.IndividualScreenRepo;
import com.gerp.usermgmt.services.usermgmt.IndividualScreenService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IndividualScreenServiceImpl implements IndividualScreenService {

    private final IndividualScreenRepo individualScreenRepo;
    private final IndividualScreenConverter individualScreenConverter;
    private final CustomMessageSource customMessageSource;

    public IndividualScreenServiceImpl(IndividualScreenRepo individualScreenRepo,
                                       IndividualScreenConverter individualScreenConverter,
                                       CustomMessageSource customMessageSource) {
        this.individualScreenRepo = individualScreenRepo;
        this.individualScreenConverter = individualScreenConverter;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public List<IndividualScreenDto> findAll() {
        return individualScreenConverter.toDto(individualScreenRepo.findAll());
    }

    @Override
    public IndividualScreenDto createIndividualScreen(IndividualScreenDto individualScreenDto) {
        IndividualScreen individualScreen = individualScreenConverter.toEntity(individualScreenDto);
        return individualScreenConverter.toDto(individualScreenRepo.save(individualScreen));
    }

    @Override
    public IndividualScreenDto findById(Long id) {
        Optional<IndividualScreen> optionalIndividualScreen = individualScreenRepo.findById(id);
        if (optionalIndividualScreen.isPresent())
            return individualScreenConverter.toDto(optionalIndividualScreen.get());
        throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("screen")));
    }

    @Override
    public List<IndividualScreenDto> findByScreenGroupId(Long screenGroupId) {
        return individualScreenConverter.toDto(individualScreenRepo.findAllByScreenGroup(screenGroupId));
    }
}
