package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.FooterDataDto;
import com.gerp.dartachalani.model.FooterData;
import com.gerp.shared.generic.api.GenericService;

import java.util.List;

public interface FooterDataService extends GenericService<FooterData, Long> {

    Long saveFooter(FooterDataDto data);

    Long updateFooter(FooterDataDto data);

    FooterDataDto getById(Long id, String lang);

    List<FooterDataDto> getByOfficeCode();

    Long toggleActive(Long id);
}
