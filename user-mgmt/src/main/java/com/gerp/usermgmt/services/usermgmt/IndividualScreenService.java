package com.gerp.usermgmt.services.usermgmt;

import com.gerp.usermgmt.pojo.IndividualScreenDto;

import java.util.List;

public interface IndividualScreenService {
    List<IndividualScreenDto> findAll();

    IndividualScreenDto createIndividualScreen(IndividualScreenDto individualScreenDto);

    IndividualScreenDto findById(Long id);

    List<IndividualScreenDto> findByScreenGroupId(Long screenGroupId);
}
