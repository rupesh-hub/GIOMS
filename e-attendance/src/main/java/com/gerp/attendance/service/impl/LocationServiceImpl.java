//package com.gerp.attendance.service.impl;
//
//import com.gerp.attendance.Converter.LocationConverter;
//import com.gerp.attendance.Pojo.EmployeeAttendancePojo;
//import com.gerp.attendance.Pojo.LocationPojo;
//import com.gerp.attendance.Pojo.SetupPojo;
//import com.gerp.attendance.mapper.LocationMapper;
//import com.gerp.attendance.model.attendances.EmployeeAttendance;
//import com.gerp.attendance.model.setup.District;
//import com.gerp.attendance.model.setup.Location;
//import com.gerp.attendance.repo.LocationRepo;
//import com.gerp.attendance.service.LocationService;
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
//public class LocationServiceImpl extends GenericServiceImpl<Location, Long> implements LocationService {
//
//    private final LocationRepo locationRepo;
//    private final LocationConverter locationConverter;
//    private final LocationMapper locationMapper;
//    private final CustomMessageSource customMessageSource;
//
//    public LocationServiceImpl(LocationRepo locationRepo, LocationConverter locationConverter, LocationMapper locationMapper, CustomMessageSource customMessageSource) {
//        super(locationRepo);
//        this.locationRepo = locationRepo;
//        this.locationMapper=locationMapper;
//        this.locationConverter=locationConverter;
//        this.customMessageSource = customMessageSource;
//    }
//
//    @Override
//    public Location findById(Long uuid) {
//        Location location = super.findById(uuid);
//        if (location == null)
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("location")));
//        return location;
//    }
//
//    @Override
//    public ArrayList<SetupPojo> getByDistrictId(Long id) {
//        return locationMapper.getAllByDistrict(id);
//    }
//
//    @Override
//    public Location save(LocationPojo locationPojo) {
//        Location location=locationConverter.toEntity(locationPojo);
//        locationRepo.save(location);
//        return location;
//    }
//
//    @Override
//    public Location update(LocationPojo locationPojo) {
//        Location update=locationRepo.findById(locationPojo.getId()).get();
//        Location location = locationConverter.toEntity(locationPojo);
//        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
//        try {
//            beanUtilsBean.copyProperties(update, location);
//        } catch (Exception e) {
//            throw new RuntimeException("id doesnot exists");
//        }
//        locationRepo.save(update);
//        return location;
//    }
//}
