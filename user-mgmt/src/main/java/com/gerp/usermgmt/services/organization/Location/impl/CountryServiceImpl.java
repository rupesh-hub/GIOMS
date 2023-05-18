package com.gerp.usermgmt.services.organization.Location.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.address.Country;
import com.gerp.usermgmt.repo.address.CountryRepo;
import com.gerp.usermgmt.services.organization.Location.CountryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CountryServiceImpl extends GenericServiceImpl<Country, String> implements CountryService {

    private final CountryRepo countryRepo;
    private final CustomMessageSource customMessageSource;

    public CountryServiceImpl(CountryRepo countryRepo, CustomMessageSource customMessageSource) {
        super(countryRepo);
        this.countryRepo = countryRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public Country findById(String uuid) {
        Country country = super.findById(uuid);
        if (country == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("country")));
        return country;
    }

    @Override
    public List<IdNamePojo> findAllActive() {
        return countryRepo.getAllCountry();
    }
}
