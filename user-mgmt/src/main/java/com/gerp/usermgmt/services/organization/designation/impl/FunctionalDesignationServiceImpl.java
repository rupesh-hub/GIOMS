package com.gerp.usermgmt.services.organization.designation.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.converter.organiztion.orgtransfer.FunctionalDesignationConverter;
import com.gerp.usermgmt.mapper.organization.DesignationMapper;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.FunctionalDesignationPojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import com.gerp.usermgmt.repo.designation.FunctionalDesignationRepo;
import com.gerp.usermgmt.services.organization.designation.FunctionalDesignationService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FunctionalDesignationServiceImpl extends GenericServiceImpl<FunctionalDesignation, String> implements FunctionalDesignationService {

    private final FunctionalDesignationRepo functionalDesignationRepo;

    private final FunctionalDesignationConverter functionalDesignationConverter;
    private final DesignationMapper designationMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private TokenProcessorService tokenProcessorService;

    public FunctionalDesignationServiceImpl(FunctionalDesignationRepo functionalDesignationRepo, FunctionalDesignationConverter functionalDesignationConverter, DesignationMapper designationMapper) {
        super(functionalDesignationRepo);
        this.functionalDesignationRepo = functionalDesignationRepo;
        this.functionalDesignationConverter = functionalDesignationConverter;
        this.designationMapper = designationMapper;
    }

    @Override
    public FunctionalDesignation findById(String id) {
        return functionalDesignationRepo.findById(id).orElseThrow(() -> new ServiceValidationException("not found"));
    }

    @Override
    public List<IdNamePojo> designationSearch(SearchPojo searchPojo) {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        Map<String, Object> searchParam = objectMapper.convertValue(searchPojo, Map.class);
        return designationMapper.getDesignationByFilter(searchParam, orgTypeId);
    }

    @Override
    public List<IdNamePojo> officeDesignationSearch(SearchPojo searchPojo) {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        Map<String, Object> searchParam = objectMapper.convertValue(searchPojo, Map.class);
        return designationMapper.getOfficeDesignationByFilter(searchParam, orgTypeId);
    }

    @Override
    public List<IdNamePojo> officeDesignationList(String officeCode) {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        return designationMapper.getOfficeDesignations(officeCode, orgTypeId);
    }

    @Override
    public List<IdNamePojo> findSectionDesignationById(Long sectionId) {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }

        return designationMapper.getSectionDesignations(sectionId, orgTypeId);
    }

    @Override
    public void deleteDesignation(String id) {
        functionalDesignationRepo.deActivateDesignation(id);
    }

    @Override
    public String save(FunctionalDesignationPojo functionalDesignationPojo) {
        FunctionalDesignation functionalDesignation = functionalDesignationConverter.toEntity(functionalDesignationPojo);
        if(functionalDesignationPojo.getOrganisationType() !=null){
            OrganisationType organisationType = new OrganisationType();
            organisationType.setId(functionalDesignationPojo.getOrganisationType());
            functionalDesignation.setOrganisationType(organisationType);
        }
        return functionalDesignationRepo.save(functionalDesignation).getCode();
    }

    @Override
    @CacheEvict(cacheNames = {OfficeCacheConst.CACHE_VALUE_DESIGNATION_POJO, OfficeCacheConst.CACHE_VALUE_DESIGNATION_DETAIL_POJO }, key = "#fp.getCode()", condition = "#fp.getCode() != null ")
    public String update(FunctionalDesignationPojo fp) {
        FunctionalDesignation f = functionalDesignationRepo.findById(fp.getCode()).orElseThrow(() -> new ServiceValidationException("crud.not_exits"));
        f = functionalDesignationConverter.toUpdateEntity(fp, f);
        return functionalDesignationRepo.save(f).getCode();
    }

    @Override
    public Page<FunctionalDesignationPojo> getFilterPaginated(GetRowsRequest paginatedRequest) {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }

        Page<FunctionalDesignationPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = designationMapper.filterPaginated(page, paginatedRequest.getSearchField(), orgTypeId);
        return page;
    }

    @Override
    public DetailPojo getDesignationByCode(String code) {
        return designationMapper.getDesignationDetailById(code);
    }
}
