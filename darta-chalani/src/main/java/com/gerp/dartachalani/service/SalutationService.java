package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.SalutationPojo;

import java.util.List;

public interface SalutationService {
    Long addSalutation(SalutationPojo standardTemplatePojo);

    List<SalutationPojo> getSalutation(String pisCode);

    Long updateSalutation(SalutationPojo standardTemplatePojo);

    Long deleteSalutation(Long id);
}
