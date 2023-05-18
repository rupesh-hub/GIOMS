package com.gerp.usermgmt.services.organization.Location.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.address.District;
import com.gerp.usermgmt.repo.address.DistrictRepo;
import com.gerp.usermgmt.services.organization.Location.DistrictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DistrictServiceImpl extends GenericServiceImpl<District, String> implements DistrictService {

    private final DistrictRepo districtRepo;
    private final CustomMessageSource customMessageSource;

    public DistrictServiceImpl(DistrictRepo districtRepo, CustomMessageSource customMessageSource) {
        super(districtRepo);
        this.districtRepo = districtRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public District findById(String uuid) {
        District district = super.findById(uuid);
        if (district == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("country")));
        return district;
    }

    @Override
    public List<District> findAllByProvinceId(Integer pId) {
//        return districtRepo.findAllByProvinceId(pId);
        return null;
    }

    @Override
    public List<IdNamePojo> findAllDistricts() {
        return districtRepo.findAllDto();
    }

    @Override
    public IdNamePojo findByIdMinimal(String code) {
        return districtRepo.findDistrictDetailMinimal(code);
    }
}
