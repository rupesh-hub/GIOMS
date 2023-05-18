package com.gerp.usermgmt.services.organization.office.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.converter.organiztion.office.OfficeConverter;
import com.gerp.usermgmt.mapper.organization.OfficeMapper;
import com.gerp.usermgmt.mapper.organization.SectionMapper;
import com.gerp.usermgmt.model.address.District;
import com.gerp.usermgmt.model.address.MunicipalityVdc;
import com.gerp.usermgmt.model.employee.SectionSubsection;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.office.OfficeType;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.model.office.OrganizationLevel;
import com.gerp.usermgmt.pojo.external.TMSOfficePojo;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeSavePojo;
import com.gerp.usermgmt.repo.office.OfficeHeadRepo;
import com.gerp.usermgmt.repo.office.OfficeRepo;
import com.gerp.usermgmt.repo.office.SectionRepo;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OfficeServiceImpl extends GenericServiceImpl<Office, String> implements OfficeService {
    private final OfficeRepo officeRepo;
    private final OfficeHeadRepo officeHeadRepo;
    private final OfficeMapper officeMapper;
    private final SectionMapper sectionMapper;
    private final DateConverter dateConverter;
    private final SectionRepo sectionRepo;
    private final OfficeConverter officeConverter;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenProcessorService tokenProcessorService;

    public OfficeServiceImpl(OfficeRepo officeRepo, OfficeHeadRepo officeHeadRepo, OfficeMapper officeMapper, SectionMapper sectionMapper, DateConverter dateConverter, SectionRepo sectionRepo, OfficeConverter officeConverter, TokenProcessorService tokenProcessorService) {
        super(officeRepo);
        this.officeRepo = officeRepo;
        this.officeHeadRepo = officeHeadRepo;
        this.officeMapper = officeMapper;
        this.sectionMapper = sectionMapper;
        this.dateConverter = dateConverter;
        this.sectionRepo = sectionRepo;
        this.officeConverter = officeConverter;
        this.tokenProcessorService = tokenProcessorService;
    }

    @Override
    public OfficePojo findByCode(String officeCode) {
        Office office = officeRepo.findByOfficeParent(officeCode);
        return getOfficePojo(office);
//        return null;
    }

    @Override
    public OfficePojo officeDetail(String officeCode) {
        return officeMapper.getOfficeByCode(officeCode);
    }

    @Override
    public List<OfficePojo> getAllTopParentOffice() {

        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }

        return officeMapper.getAllParentOffice(orgTypeId);
    }


    @Override
    public Page<OfficePojo> allOffices(int limit, int page, String searchKey, String district) {
        Page<OfficePojo> officePojoPage = new Page<>(page, limit);
        return officeMapper.getALlOffices(officePojoPage, searchKey, district);
    }

    @Override
    public Page<OfficePojo> allOfficePaginated(GetRowsRequest paginatedRequest) {
        Page<OfficePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        page = officeMapper.searchOfficePaginated(page, paginatedRequest.getSearchField(), orgTypeId);
        return page;
    }

    @Override
    public OfficePojo officeSectionListByCode(String officeCode) {
        OfficePojo officePojo = officeMapper.getOfficeByCode(officeCode);
        officePojo.setChildOffice(officeMapper.getChildOffices(officeCode));
        officePojo.setSection(sectionMapper.getSubsectionByOfficeCode(officeCode));
        return officePojo;
    }

    @Override
    public List<OfficePojo> officeLowerHierarchyList(String officeCode) {
        List<OfficePojo> officeSubOffices = this.officeMapper.getChildOffices(officeCode);
        for (OfficePojo officeSubOffice : officeSubOffices) {
            getSubOffice(officeSubOffice);
        }
        return officeSubOffices;
    }

    @Override
    public List<String> getLowerOfficeEmployee(String officeCode) {
        List<String> employeeList = new ArrayList<>();
        officeMapper.getChildOffices(officeCode).stream().forEach(o -> {
            List<String> listOfOffice = new ArrayList<>();
            listOfOffice.addAll(officeMapper.getAllChildOfficeCode(o.getCode()));
            employeeList.addAll(officeMapper.getEmployeeList(listOfOffice));
        });
        return employeeList;
    }

    @Override
    public List<OfficePojo> officeHigherHierarchyListOnly(String officeCode) {
        List<String> excludeOffices = Arrays.asList("00", "8886", "22490", "22491");
        List<String> officeIds = officeMapper.getAllParentOfficeIds(officeCode);
        officeIds = officeIds.stream().filter(s -> !excludeOffices.contains(s)).collect(Collectors.toList());
        if (officeIds.isEmpty()) {
            throw new RuntimeException("No Higher Office Found");
        }
        return officeMapper.getALlOfficesByIds(officeIds);
    }

    @Override
    public List<OfficePojo> officeHigherHierarchyListOnly() {
        List<String> excludeOffices = Arrays.asList("00");
//        List<String> excludeOffices = Arrays.asList("8886", "22490", "22491");
        String topOffice = officeMapper.getTopLevelOffice(tokenProcessorService.getOfficeCode(), excludeOffices);
        if (topOffice == null) {
            return Collections.emptyList();
        }
        List<OfficePojo> offices = officeMapper.getALlOfficesByIds(Arrays.asList(topOffice, tokenProcessorService.getOfficeCode()));
        offices.forEach(officePojo -> officePojo.setTopLevelOffice(officePojo.getCode().equals(topOffice)));
        return offices;
    }

    @Override
    public List<OfficePojo> getChildOffice(String officeCode) {
        return officeMapper.getChildOffice(officeCode);
    }

    @Override
    public List<OfficePojo> getParentOffices(String officeCode) {
        return officeMapper.getParentOffice(officeCode);
    }

    @Override
    public List<OfficePojo> getMinistryOffices() {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        List<String> ids = officeMapper.getAllParentOffice(orgTypeId).stream().map(OfficePojo::getCode).collect(Collectors.toList());
        return officeMapper.getMinistryOffices(ids);
    }

    @Override
    public List<OfficePojo> officeListByParams(SearchPojo searchPojo) {
        Map<String, Object> searchParam = objectMapper.convertValue(searchPojo, Map.class);
        return officeMapper.getOfficeByFilter(searchParam);
    }

    @Override
    public List<OfficePojo> officeActiveStatus(List<String> officeCodes) {
        List<OfficePojo> offices = officeMapper.getALlOfficesByIds(officeCodes);
        for (OfficePojo officePojo : offices) {
            officePojo.setIsActiveOffice(!ObjectUtils.isEmpty(officeHeadRepo.findByOfficeCode(officePojo.getCode())));
        }
        return offices;
    }

    @Override
    public String saveOffice(OfficeSavePojo officePojo) {
        Office office = officeConverter.toOfficeEntity(officePojo);
        return officeRepo.save(office).getCode();
    }

    @CacheEvict(cacheNames = {OfficeCacheConst.CACHE_VALUE_MINIMAL , OfficeCacheConst.CACHE_VALUE_DETAIL, OfficeCacheConst.CACHE_VALUE_DETAIL_EATT}
            , key = "#officePojo.getCode()")
    @Override
    public String updateOffice(OfficeSavePojo officePojo) {
        Office office = officeRepo.findById(officePojo.getCode()).orElseThrow(() -> new RuntimeException("employee not found"));
        if (!ObjectUtils.isEmpty(officePojo.getMunicipalityVdcCode())) {
            office.setMunicipalityVdc(new MunicipalityVdc());
        }
        if (!ObjectUtils.isEmpty(officePojo.getDistrictCode())) {
            office.setDistrict(new District());
        }
        if (!ObjectUtils.isEmpty(officePojo.getOfficeTypeId())) {
            office.setOfficeType(new OfficeType());
        }
        if (!ObjectUtils.isEmpty(officePojo.getOrganizationLevelId())) {
            office.setOrganizationLevel(new OrganizationLevel());
        }
        if (!ObjectUtils.isEmpty(officePojo.getOrganisationTypeId())) {
            office.setOrganisationType(new OrganisationType());
        }
        if (!ObjectUtils.isEmpty(officePojo.getParentCode())) {
            // todo remove this
            Office pOffice = new Office(officePojo.getParentCode());
            office.setParent(pOffice);
        }
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        try {
            mapper.map(officePojo, office);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("id doesnot exists");
        }
        return officeRepo.save(office).getCode();
    }

    @Override
    public OfficePojo officeMinimalDetail(String officeCode) {
        return officeMapper.getOfficeDetailMinimal(officeCode);
    }

    @Override
    public boolean activateOffice(String officeCode) {
        Office o = officeRepo.findById(officeCode).
                orElseThrow(() -> new ServiceValidationException("No office found for provided office code"));


        if (Boolean.TRUE.equals(o.getIsGiomsActive())) {
            officeRepo.deActivateOffice(officeCode);
        } else {
            officeRepo.activateOffice(officeCode);
            if (ObjectUtils.isEmpty(sectionMapper.getSectionMinimalByOfficeCode(officeCode))) {
                SectionSubsection ss = new SectionSubsection();
                ss.setNameEn(o.getNameEn());
                ss.setNameNp(o.getNameNp());
                ss.setOffice(o);
                ss.setApproved(true);
                ss.setApprovedDateEN(LocalDate.now());
                ss.setApprovedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
                ss.setParent(null);
                sectionRepo.save(ss);
            }
        }
        return true;
    }

    @Override
    public void updateSetupStatus() {
        officeRepo.updateSetupStatus(tokenProcessorService.getOfficeCode());
    }

    @Override
    public boolean getSetUpStatus() {
        Boolean value = officeRepo.getBySetupCompleted(tokenProcessorService.getOfficeCode());
        return value != null ? value : Boolean.FALSE;
    }

    @Override
    public List<TMSOfficePojo> getAllGIOMSActiveOffice() {
        return (List) officeMapper.getAllOfficeByGIOMSStatus(true);
    }

    private void getSubOffice(OfficePojo office) {
        List<OfficePojo> childOffices =
                officeMapper.getChildOffices(office.getCode());
        for (OfficePojo childOffice : childOffices) {
            getSubOffice(childOffice);
        }
        office.setChildOffice(childOffices);
    }

    private OfficePojo getOfficePojo(Office office) {
        List<String> excludeOffices = Arrays.asList("00", "8886", "22490", "22491");
        OfficePojo pojo = new OfficePojo();
        pojo.setNameEn(office.getNameEn());
        pojo.setCode(office.getCode());
        pojo.setNameNp(office.getNameNp());
        if (null != office.getParent() && !excludeOffices.contains(office.getParent().getCode())) {
            pojo.setParentOffice(getOfficePojo(office.getParent()));
        }
        return pojo;
    }

    @Override
    public List<TMSOfficePojo> getAllOfficeByOfficeCodes(List<String> offcieCodes) {

        List<TMSOfficePojo> officePojos = officeMapper.getAllOfficeByCodes(offcieCodes);
        return officePojos;
    }

    @Override
    public List<OfficePojo> getGiomsActiveOfficeList() {
        return officeMapper.getGiomsActiveOffice();
    }
}
