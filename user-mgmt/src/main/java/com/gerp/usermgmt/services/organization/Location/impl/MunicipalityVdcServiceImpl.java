package com.gerp.usermgmt.services.organization.Location.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.mapper.organization.MunicipalityMapper;
import com.gerp.usermgmt.model.address.MunicipalityVdc;
import com.gerp.usermgmt.repo.address.MunicipalityVdcRepo;
import com.gerp.usermgmt.services.organization.Location.MunicipalityVdcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MunicipalityVdcServiceImpl extends GenericServiceImpl<MunicipalityVdc, String> implements MunicipalityVdcService {

    private final MunicipalityVdcRepo municipalityVdcRepo;
    private final CustomMessageSource customMessageSource;
    private final MunicipalityMapper municipalityMapper;

    public MunicipalityVdcServiceImpl(MunicipalityVdcRepo municipalityVdcRepo, CustomMessageSource customMessageSource, MunicipalityMapper municipalityMapper) {
        super(municipalityVdcRepo);
        this.municipalityVdcRepo = municipalityVdcRepo;
        this.customMessageSource = customMessageSource;
        this.municipalityMapper = municipalityMapper;
    }

    @Override
    public List<IdNamePojo> municiplityByDistrict(String districtCode) {
        return municipalityMapper.municipalityByDistrictCode(districtCode);
    }
}
