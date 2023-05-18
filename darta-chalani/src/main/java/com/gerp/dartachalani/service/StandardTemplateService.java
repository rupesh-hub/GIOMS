package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.template.StandardTemplatePojo;

import java.util.List;

public interface StandardTemplateService {
    int addStandardTemplate(StandardTemplatePojo standardTemplatePojo);

    List<StandardTemplatePojo> getStandardTemplate();

    StandardTemplatePojo getStandardTemplateById(int id);

    int updateStandardTemplate(StandardTemplatePojo standardTemplatePojo);

    int deleteStandardTemplateById(int id);
}
