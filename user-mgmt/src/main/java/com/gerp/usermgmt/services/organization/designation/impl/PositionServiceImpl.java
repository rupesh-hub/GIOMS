package com.gerp.usermgmt.services.organization.designation.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.converter.organiztion.orgtransfer.PositionConverter;
import com.gerp.usermgmt.mapper.organization.PositionMapper;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.PositionPojo;
import com.gerp.usermgmt.repo.designation.PositionRepo;
import com.gerp.usermgmt.services.organization.designation.PositionService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = {"positionCodeNode", "positionCodeNodeSelf"})
public class PositionServiceImpl extends GenericServiceImpl<Position, String> implements PositionService {
    private final PositionRepo positionRepo;
    private final PositionMapper positionMapper;
    private final TokenProcessorService tokenProcessorService;
    private final PositionConverter positionConverter;

    public PositionServiceImpl(PositionRepo positionRepo, PositionMapper positionMapper, TokenProcessorService tokenProcessorService, PositionConverter positionConverter) {
        super(positionRepo);
        this.positionRepo = positionRepo;
        this.positionMapper = positionMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.positionConverter = positionConverter;
    }

    @Override
    public List<IdNamePojo> positions() {
        OrganisationType organisationType = null;
        if (!tokenProcessorService.isAdmin()) {
            Long orgTypeId = tokenProcessorService.getOrganisationTypeId();
            organisationType = new OrganisationType(orgTypeId);
        }
        return positionRepo.positions(organisationType);
    }

    @Override
    public List<IdNamePojo> topParentPosition() {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        return positionMapper.getParentPositions(orgTypeId);
    }

    @Override
    @Cacheable(value = "positionCodeNode", key = "#positionCode")
    public List<String> getAllNodePositionCode(String positionCode, Long orderNo) {
        String highestPositionCode = positionMapper.getHighestParent(positionCode);
        List<String> getAllNodePositionCode = positionMapper.getAllNodePositionCode(highestPositionCode + "%", positionCode, orderNo);
        return getAllNodePositionCode;
    }

    @Override
    public List<String> getAllNodePositionCodeWithSelf(String positionCode, Long orderNo) {
        String highestPositionCode = positionMapper.getHighestParent(positionCode);
        List<String> getAllNodePositionCode = positionMapper.getAllNodePositionCodeWithSelf(highestPositionCode + "%", orderNo);
        return getAllNodePositionCode;
    }

    @Override
    public List<String> getAllNodePositionCodeWithOutSelf(String positionCode, Long orderNo) {
        String highestPositionCode = positionMapper.getHighestParent(positionCode);
        List<String> getAllNodePositionCode = positionMapper.getAllNodePositionCodeWithSelf(highestPositionCode + "%", orderNo);
        return getAllNodePositionCode;
    }

    @Override
    public List<String> getAllLowerNodePositionCodeWithSelf(String positionCode, Long orderNo) {
        String highestPositionCode = positionMapper.getHighestParent(positionCode);
        List<String> getAllNodePositionCode = positionMapper.getAllLowerNodePositionCodeWithSelf(highestPositionCode + "%", orderNo);
        return getAllNodePositionCode;
    }

    @Override
    public PositionPojo positionDetailByPis(String pisCode) {
        return positionMapper.getPositionByPisCode(pisCode);
    }

    @Override
    public List<IdNamePojo> getOfficePositions() {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        return positionMapper.getOfficePosition(tokenProcessorService.getOfficeCode(), orgTypeId);
    }

    @Override
    public String save(PositionPojo position) {
        return positionRepo.save(positionConverter.toEntity(position)).getCode();
    }

    @Override
    @CacheEvict(value = OfficeCacheConst.CACHE_VALUE_POSITION_POJO, key = "#dto.getCode()", condition = "#dto.getCode() !=null")
    public String update(PositionPojo dto) {
        Position position = positionRepo.findById(dto.getCode()).orElseThrow(() -> new ServiceValidationException(""));
        position = positionConverter.toUpdateEntity(dto, position);
        return positionRepo.save(position).getCode();
    }

    @Override
    public List<PositionPojo> positionSearch(SearchPojo searchPojo) {
        if (searchPojo.getOrganisationTypeId() == null) {
            searchPojo.setOrganisationTypeId(tokenProcessorService.getOrganisationTypeId());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> searchParam = objectMapper.convertValue(searchPojo, Map.class);
        return positionMapper.getPositionFiltered(searchParam, tokenProcessorService.getOrganisationTypeId());
    }
}
