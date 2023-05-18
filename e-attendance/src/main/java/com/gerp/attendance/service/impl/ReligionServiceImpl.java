//package com.gerp.attendance.service.impl;
//
//import com.gerp.attendance.model.setup.Religion;
//import com.gerp.attendance.repo.ReligionRepo;
//import com.gerp.attendance.service.ReligionService;
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.generic.service.GenericServiceImpl;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * @author Rohit Sapkota
// * @version 1.0.0
// * @since 1.0.0
// */
//@Service
//@Transactional
//public class ReligionServiceImpl extends GenericServiceImpl<Religion, Integer> implements ReligionService {
//
//    private final ReligionRepo religionRepo;
//    private final CustomMessageSource customMessageSource;
//
//    public ReligionServiceImpl(ReligionRepo religionRepo, CustomMessageSource customMessageSource) {
//        super(religionRepo);
//        this.religionRepo = religionRepo;
//        this.customMessageSource = customMessageSource;
//    }
//
//    @Override
//    public Religion findById(Integer uuid) {
//        Religion religion = super.findById(uuid);
//        if (religion == null)
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("religion")));
//        return religion;
//    }
//
//}
