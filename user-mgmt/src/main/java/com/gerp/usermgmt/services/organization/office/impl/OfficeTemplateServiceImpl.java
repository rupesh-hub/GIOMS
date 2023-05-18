package com.gerp.usermgmt.services.organization.office.impl;

import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.constant.OfficeConstants;
import com.gerp.usermgmt.converter.organiztion.office.OfficeTemplateConverter;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.office.OfficeTemplate;
import com.gerp.usermgmt.pojo.organization.office.OfficeTemplatePojo;
import com.gerp.usermgmt.repo.office.OfficeTemplateRepo;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import com.gerp.usermgmt.services.organization.office.OfficeTemplateService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Service
public class OfficeTemplateServiceImpl extends GenericServiceImpl<OfficeTemplate, Long> implements OfficeTemplateService {
    private  final OfficeTemplateRepo officeTemplateRepo;
    private final OfficeService officeService;
    private final TokenProcessorService tokenProcessorService;

    private final OfficeTemplateConverter officeTemplateConverter;
    public OfficeTemplateServiceImpl(OfficeTemplateRepo officeTemplateRepo, OfficeService officeService, TokenProcessorService tokenProcessorService, OfficeTemplateConverter officeTemplateConverter) {
        super(officeTemplateRepo);
        this.officeTemplateRepo = officeTemplateRepo;
        this.officeService = officeService;
        this.tokenProcessorService = tokenProcessorService;
        this.officeTemplateConverter = officeTemplateConverter;
    }

    @Override
    @Transactional
    public Boolean changeActiveTemplate(Long officeTemplateId) {
        OfficeTemplate o = officeTemplateRepo.findById(officeTemplateId).orElseThrow(() -> new NullPointerException("template not found"));
        officeTemplateRepo.updateOfficeTemplate(o.getOffice().getCode(), o.getType());
        o.setActive(!o.getActive());
        officeTemplateRepo.save(o);
        return true;
    }

    @Override
    @Transactional
    public Long save(OfficeTemplate officeTemplate) {
        if(!ObjectUtils.isEmpty(officeTemplate.getParentId())) {
            // refactor this to same function
            officeTemplateRepo.suspendOfficeTemplate(officeTemplate.getParentId(), tokenProcessorService.getOfficeCode());
            officeTemplateRepo.updateOfficeTemplate(tokenProcessorService.getOfficeCode(), officeTemplate.getType());
        } else {
            officeTemplate.setActive(Boolean.FALSE);
        }
        if(tokenProcessorService.isAdmin() || tokenProcessorService.isOrganisationAdmin()) {
            officeTemplate.setIsGlobalTemplate(Boolean.TRUE);
        }
        OfficeTemplate o =  officeTemplateRepo.save(officeTemplate);
        return o.getId();
    }

    @Override
    public Long saveDefault(OfficeTemplate officeTemplate) {
//        if(officeTemplateRepo.findActiveTemplateCountByOffice
//                (OfficeConstants.ADMIN_OFFICE, officeTemplate.getType()) > 0) {
//            throw new ServiceValidationException("Active Template Exists for Default");
//        }
        officeTemplate.setActive(Boolean.FALSE);
        OfficeTemplate o =  officeTemplateRepo.save(officeTemplate);
        return o.getId();
    }

    @Override
    public Long updateOfficeTemplate(OfficeTemplatePojo officeTemplate) {
        officeTemplate.setIsActive(null);
        OfficeTemplate o = officeTemplateRepo.findById(officeTemplate.getId()).orElseThrow(() -> new NullPointerException("Template not found"));
        try {
            BeanUtils.copyProperties(o, officeTemplate);
            o.setOffice(new Office(officeTemplate.getOfficeCode()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return officeTemplateRepo.save(o).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficeTemplatePojo> findByOfficeCode(String officeCode, TemplateType type) {
        return officeTemplateRepo.findByOfficeId(officeCode, type);
    }

    @Override
    @Transactional(readOnly = true)
    public OfficeTemplatePojo findActiveByOfficeCode(String officeCode,TemplateType type) {
        return officeTemplateRepo.findByActiveOfficeId(officeCode, type, tokenProcessorService.getOrganisationTypeId());
    }

    @Override
    @Transactional(readOnly = true)
    public OfficeTemplatePojo findDefaultTemplate(TemplateType type) {
        Long organisationTypeId = tokenProcessorService.getOrganisationTypeId();
        OfficeTemplatePojo officeTemplatePojo = officeTemplateRepo.findGlobalByActiveOfficeId(type, organisationTypeId);
        if(type == TemplateType.H) {
            officeTemplatePojo.setHierarchyOffices(officeService.officeHigherHierarchyListOnly());
        }
        return officeTemplatePojo;
    }
}
