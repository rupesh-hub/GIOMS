//package com.gerp.attendance.controller.device;
//
//import com.gerp.attendance.constant.PermissionConstants;
////import com.gerp.attendance.model.device.AttendanceDeviceType;
//import com.gerp.attendance.service.AttendanceDeviceService;
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.generic.controllers.generic.GenericCrudController;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author Rohit Sapkota
// * @version 1.0.0
// * @since 1.0.0
// */
//@RestController
//@RequestMapping("/attendance-device-type")
//public class AttendanceDeviceTypeController extends GenericCrudController<AttendanceDeviceType, Integer> {
//    private final AttendanceDeviceService attendanceDeviceService;
//    private final CustomMessageSource customMessageSource;
//
//    public AttendanceDeviceTypeController(AttendanceDeviceService attendanceDeviceService, CustomMessageSource customMessageSource) {
//        this.attendanceDeviceService = attendanceDeviceService;
//        this.customMessageSource = customMessageSource;
//        this.moduleName= PermissionConstants.ATTENDANCE_DEVICE_TYPE_MODULE_NAME;
//        this.permissionName= PermissionConstants.DEVICE+"_"+PermissionConstants.ATTENDANCE_DEVICE_TYPE_SETUP;
//    }
//}
