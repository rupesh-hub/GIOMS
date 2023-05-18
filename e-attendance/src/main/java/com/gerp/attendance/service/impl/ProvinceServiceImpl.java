//package com.gerp.attendance.service.impl;
//
//import com.gerp.attendance.Converter.ProvinceConverter;
//import com.gerp.attendance.Pojo.ProvincePojo;
//import com.gerp.attendance.Pojo.SetupPojo;
//import com.gerp.attendance.mapper.ProvinceMapper;
//import com.gerp.attendance.model.setup.Province;
//import com.gerp.attendance.repo.ProvinceRepo;
//import com.gerp.attendance.service.ProvinceService;
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
//public class ProvinceServiceImpl extends GenericServiceImpl<Province, Long> implements ProvinceService {
//
//    private final ProvinceRepo provinceRepo;
//    private final ProvinceConverter provinceConverter;
//    private final ProvinceMapper provinceMapper;
//    private final CustomMessageSource customMessageSource;
//
//    public ProvinceServiceImpl(ProvinceRepo provinceRepo, ProvinceConverter provinceConverter, ProvinceMapper provinceMapper, CustomMessageSource customMessageSource) {
//        super(provinceRepo);
//        this.provinceRepo = provinceRepo;
//        this.provinceConverter = provinceConverter;
//        this.provinceMapper = provinceMapper;
//        this.customMessageSource = customMessageSource;
//    }
//
//    @Override
//    public Province findById(Long uuid) {
//        Province province = super.findById(uuid);
//        if (province == null)
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("province")));
//        return province;
//    }
//
//    @Override
//    public ArrayList<SetupPojo> getByCountryId(Long id) {
//        return provinceMapper.getByCountryId(id);
//    }
//
//    @Override
//    public Province save(ProvincePojo provincePojo) {
//        Province province = provinceConverter.toEntity(provincePojo);
//        provinceRepo.save(province);
//        return province;
//    }
//
//    @Override
//    public Province update(ProvincePojo provincePojo) {
//        Province update = provinceRepo.findById(provincePojo.getId()).get();
//        Province province = provinceConverter.toEntity(provincePojo);
//        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
//        try {
//            beanUtilsBean.copyProperties(update, province);
//        } catch (Exception e) {
//            throw new RuntimeException("id doesnot exists");
//        }
//        provinceRepo.save(update);
//        return province;
//    }
//}
