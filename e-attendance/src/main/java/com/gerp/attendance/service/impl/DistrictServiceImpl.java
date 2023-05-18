//package com.gerp.attendance.service.impl;
//
//import com.gerp.attendance.Converter.DistrictConverter;
//import com.gerp.attendance.Pojo.DistrictPojo;
//import com.gerp.attendance.Pojo.SetupPojo;
//import com.gerp.attendance.mapper.DistrictMapper;
//import com.gerp.attendance.model.setup.District;
//import com.gerp.attendance.repo.DistrictRepo;
//import com.gerp.attendance.service.DistrictService;
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.generic.service.GenericServiceImpl;
//import com.gerp.shared.utils.NullAwareBeanUtilsBean;
//import org.apache.commons.beanutils.BeanUtilsBean;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//
//@Service
//@Transactional
//public class DistrictServiceImpl extends GenericServiceImpl<District, Long> implements DistrictService {
//
//    private final DistrictRepo districtRepo;
//    private final DistrictConverter districtConverter;
//    private final DistrictMapper districtMapper;
//    private final CustomMessageSource customMessageSource;
//
//    public DistrictServiceImpl(DistrictRepo districtRepo, DistrictConverter districtConverter, DistrictMapper districtMapper,CustomMessageSource customMessageSource) {
//        super(districtRepo);
//        this.districtRepo = districtRepo;
//        this.districtMapper=districtMapper;
//        this.districtConverter=districtConverter;
//        this.customMessageSource = customMessageSource;
//    }
//
//    @Override
//    public District findById(Long uuid) {
//        District district = super.findById(uuid);
//        if (district == null)
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("district")));
//        return district;
//    }
//
//    @Override
//    public ArrayList<SetupPojo> getByProvinceId(Long id) {
//        return districtMapper.getAllByProvince(id);
//    }
//
////    @Override
////    public ArrayList<SetupPojo> getAllDistrict() {
////        return districtMapper.getAllDistrict();
////    }
//
//    @Override
//    public District save(DistrictPojo districtPojo) {
//        District district=districtConverter.toEntity(districtPojo);
//        districtRepo.save(district);
//        return district;
//    }
//
//    @Override
//    public District update(DistrictPojo districtPojo) {
//        District update = districtRepo.findById(districtPojo.getId()).get();
//        District district = districtConverter.toEntity(districtPojo);
//        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
//        try {
//            beanUtilsBean.copyProperties(update, district);
//        } catch (Exception e) {
//            throw new RuntimeException("id doesnot exists");
//        }
//        districtRepo.save(update);
//        return district;
//    }
//}
