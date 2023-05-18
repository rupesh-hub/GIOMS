package com.gerp.usermgmt.services.organization.office.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.converter.organiztion.office.SectionConverter;
import com.gerp.usermgmt.mapper.organization.SectionMapper;
import com.gerp.usermgmt.model.employee.SectionSubsection;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.pojo.organization.office.SectionPojo;
import com.gerp.usermgmt.repo.office.SectionRepo;
import com.gerp.usermgmt.services.organization.office.SectionService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class SectionServiceImpl extends GenericServiceImpl<SectionSubsection , Long> implements SectionService  {

    private static final Logger LOG = Logger.getLogger(SectionServiceImpl.class);
    @Autowired
    private TokenProcessorService tokenProcessorService;
    private CustomMessageSource customMessageSource;

    private final SectionRepo sectionRepo;
    private final SectionMapper sectionMapper;
    private final SectionConverter sectionConverter;

    private String moduleName = PermissionConstants.SECTION;


    public SectionServiceImpl(SectionRepo sectionRepo, SectionMapper sectionMapper, SectionConverter sectionConverter) {
        super(sectionRepo);
        this.sectionRepo = sectionRepo;
        this.sectionMapper = sectionMapper;
        this.sectionConverter = sectionConverter;
    }

    @Override
    public List<SectionSubsection> findSectionHierarchyByCode(String code) {
        return sectionRepo.findAll();
    }

    @Override
    public ArrayList<SectionPojo> getSectionSubsectionOfOffice(String officeCode) {
        ArrayList<SectionPojo> sectionSubsections = this.sectionMapper.getParentSectionSubsection(officeCode);
        for (SectionPojo sectionSubsection : sectionSubsections) {
            getSubsections(sectionSubsection);
        }
        return sectionSubsections;
    }

    @Override
    public ArrayList<SectionPojo> getSectionSubsectionWithDarbandi(String officeCode) {
        ArrayList<SectionPojo> sectionSubsections = this.sectionMapper.getParentSectionSubSectionWithDesignation(officeCode);
        for (SectionPojo sectionSubsection : sectionSubsections) {
            getSubsectionsWithDarbandi(sectionSubsection);
        }
        return sectionSubsections;
    }

    @Override
    public SectionPojo getSectionSubsectionById(Long id) {
        Optional<SectionSubsection> section = sectionRepo.findById(id);
        if(section.isPresent()) {
            return sectionConverter.toDto(section.get());
        } else {
            throw new NullPointerException("id doesnt exist");
        }
    }

    @Override
    public Long addSection(SectionPojo sectionPojo) {
        SectionSubsection sectionSubsection = sectionConverter.toEntity(sectionPojo);
        sectionSubsection.setOffice(new Office(tokenProcessorService.getOfficeCode()));
         return sectionRepo.save(sectionSubsection).getId();
    }

    @Override
    @CacheEvict(value = OfficeCacheConst.CACHE_VALUE_SECTION, key = "#sectionPojo.getId()", condition = "#sectionPojo.getId() != null")
    public String update(SectionPojo sectionPojo) {
        SectionSubsection sectionSubsection = sectionRepo.findById(sectionPojo.getId()).
                orElseThrow(() -> new CustomException(customMessageSource.get("error.cant.update", moduleName)));
        if(!sectionSubsection.getOffice().getCode().equals(tokenProcessorService.getOfficeCode())){
            throw new ServiceValidationException(customMessageSource.get("error.cant.update", moduleName));
        }
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(sectionSubsection, sectionConverter.toEntity(sectionPojo));
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException("id doesnot exists");
        }
        sectionSubsection.setOffice(new Office(tokenProcessorService.getOfficeCode()));
        return sectionRepo.save(sectionSubsection).getCode();
    }

    @Override
    public List<SectionPojo> getSectionListByOffice(String officeCode) {
        return sectionMapper.getSectionMinimalByOfficeCode(officeCode);
    }

    @Override
    public List<SectionPojo> getSectionListByLoggedOffice() {
        return sectionRepo.getAllByOfficeCode(tokenProcessorService.getOfficeCode());
    }

    @Override
    public List<SectionPojo> getSubSection(Long sectionId) {
        return sectionMapper.getSectionSubsectionByParent(sectionId);
    }

    @Override
    public List<SectionPojo> getParentSectionOfOffice(String officeCode) {
        return sectionMapper.getParentSectionOfOffice(officeCode);
    }

    @Override
    public ArrayList<SectionPojo> getSectionSubsectionWithDarbandiParents(String officeCode) {
       return this.sectionMapper.getParentSectionSubSectionWithDesignation(officeCode);
    }

    @Override
    public ArrayList<SectionPojo> getSubsectionWithDarbandiByParents(Long id) {
        return sectionMapper.getSectionSubsectionByParentWithDarbandiOrdered(id);
    }

    @Override
    public List<SectionPojo> getSubsectionByEmployee() {
        return null;
    }

    @Override
    public List<SectionPojo> getSectionListByEmployee(String pisCode) {
        return sectionMapper.getSectionSubsectionByEmployee(pisCode);
    }


    @Override
    @CacheEvict(value = OfficeCacheConst.CACHE_VALUE_SECTION, key = "#id", condition = "#id != null")
    public void deleteById(Long id) {
        SectionSubsection sectionSubsection = sectionRepo.findById(id).orElseThrow( () -> new CustomException("Section not found"));
        if(sectionMapper.getSectionSubsectionCount(id) > 0) {
            throw new RuntimeException("Please Remove Designation Setup first");
        }
        List<Long> childSectionIds = deleteAllChildSections(id);
        log.info("Sections with these ids are deactivated " + childSectionIds);
        sectionRepo.deactivateSection(id);
        log.info("Section with id " + id + "deactivated ");
    }

    private void getSubsections(SectionPojo sectionSubsection) {
        ArrayList<SectionPojo> childSectionSubsections =
                    sectionMapper.getSectionSubsectionByParent(
                        sectionSubsection.getId());
        for (SectionPojo childSectionSubsection : childSectionSubsections) {
            getSubsections(childSectionSubsection);
        }
        sectionSubsection.setSubsection(childSectionSubsections);
    }

    private void getSubsectionsWithDarbandi(SectionPojo sectionSubsection) {
        ArrayList<SectionPojo> childSectionSubsections =
                    sectionMapper.getSectionSubsectionByParentWithDarbandiOrdered(sectionSubsection.getId());
        for (SectionPojo childSectionSubsection : childSectionSubsections) {
            getSubsectionsWithDarbandi(childSectionSubsection);
        }
        sectionSubsection.setSubsection(childSectionSubsections);
    }

    private List<Long> deleteAllChildSections(Long id){
        List<Long> sectionIds = sectionMapper.getChildSectionIds(id);

        if(sectionIds != null && sectionIds.size() != 0){
            sectionIds.stream().forEach(sectionid -> {
                if(sectionMapper.getSectionSubsectionCount(sectionid) > 0) {
                    throw new RuntimeException("Please Remove Designation Setup from child section");
                }
            });
            sectionRepo.deactivateAllSections(sectionIds);
        }
        return sectionIds;
    }

}
