package com.gerp.usermgmt.services.organization.Location.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.address.Province;
import com.gerp.usermgmt.repo.address.ProvinceRepo;
import com.gerp.usermgmt.services.organization.Location.ProvinceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProvinceServiceImpl extends GenericServiceImpl<Province, Integer> implements ProvinceService {

    private final ProvinceRepo provinceRepo;
    private final CustomMessageSource customMessageSource;

    public ProvinceServiceImpl(ProvinceRepo provinceRepo, CustomMessageSource customMessageSource) {
        super(provinceRepo);
        this.provinceRepo = provinceRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public Province findById(Integer uuid) {
        Province province = super.findById(uuid);
        if (province == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("country")));
        return province;
    }

    @Override
    public List<Province> findAllByCountryId(Integer cId) {
        return null;
    }
}
