//package com.gerp.attendance.service.impl;
//
//import com.gerp.attendance.model.setup.Country;
//import com.gerp.attendance.repo.CountryRepo;
//import com.gerp.attendance.service.CountryService;
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.generic.service.GenericServiceImpl;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//
//@Service
//@Transactional
//public class CountryServiceImpl extends GenericServiceImpl<Country, Long> implements CountryService {
//
//    private final CountryRepo countryRepo;
//    private final CustomMessageSource customMessageSource;
//
//    public CountryServiceImpl(CountryRepo countryRepo, CustomMessageSource customMessageSource) {
//        super(countryRepo);
//        this.countryRepo = countryRepo;
//        this.customMessageSource = customMessageSource;
//    }
//
//    @Override
//    public Country findById(Long uuid) {
//        Country country = super.findById(uuid);
//        if (country == null)
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("country")));
//        return country;
//    }
//}
