//package com.gerp.attendance.service.impl;
//
//import com.gerp.attendance.model.device.AttendanceDeviceType;
//import com.gerp.attendance.repo.AttendanceDeviceTypeRepo;
//import com.gerp.attendance.service.AttendanceDeviceTypeService;
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
//public class AttendanceDeviceTypeServiceImpl extends GenericServiceImpl<AttendanceDeviceType, Integer> implements AttendanceDeviceTypeService {
//
//    private final AttendanceDeviceTypeRepo attendanceDeviceTypeRepo;
//    private final CustomMessageSource customMessageSource;
//
//    public AttendanceDeviceTypeServiceImpl(AttendanceDeviceTypeRepo attendanceDeviceTypeRepo, CustomMessageSource customMessageSource) {
//        super(attendanceDeviceTypeRepo);
//        this.attendanceDeviceTypeRepo = attendanceDeviceTypeRepo;
//        this.customMessageSource = customMessageSource;
//    }
//
//    @Override
//    public AttendanceDeviceType findById(Integer uuid) {
//        AttendanceDeviceType attendanceDeviceType = super.findById(uuid);
//        if (attendanceDeviceType == null)
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("attendance.device.type")));
//        return attendanceDeviceType;
//    }
//}
