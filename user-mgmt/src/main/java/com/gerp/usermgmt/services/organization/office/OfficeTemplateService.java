package com.gerp.usermgmt.services.organization.office;

import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.office.OfficeTemplate;
import com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo;

import java.util.List;

public interface OfficeTemplateService extends GenericService<OfficeTemplate, Long> {
    Boolean changeActiveTemplate(Long officeTemplateId);
    Long save(OfficeTemplate officeTemplate);
    Long saveDefault(OfficeTemplate officeTemplate);
    Long updateOfficeTemplate(OfficeTemplatePojo officeTemplate);

    List<OfficeTemplatePojo> findByOfficeCode(String officeCode, TemplateType type);
     OfficeTemplatePojo findActiveByOfficeCode(String officeCode,TemplateType type);
     OfficeTemplatePojo findDefaultTemplate(TemplateType type);
}
