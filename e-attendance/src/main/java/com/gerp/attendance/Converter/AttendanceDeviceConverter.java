//package com.gerp.attendance.Converter;
//
//import com.gerp.attendance.Pojo.AttendanceDevicePojo;
//import com.gerp.attendance.model.device.AttendanceDevice;
//import com.gerp.attendance.model.device.AttendanceDeviceType;
//import com.gerp.attendance.repo.AttendanceDeviceRepo;
//import com.gerp.attendance.repo.AttendanceDeviceTypeRepo;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@Component
//public class AttendanceDeviceConverter {
//
//    private final AttendanceDeviceRepo attendanceDeviceRepo;
//    private final AttendanceDeviceTypeRepo attendanceDeviceTypeRepo;
//
//    public AttendanceDeviceConverter(AttendanceDeviceRepo attendanceDeviceRepo, AttendanceDeviceTypeRepo attendanceDeviceTypeRepo) {
//        this.attendanceDeviceRepo=attendanceDeviceRepo;
//        this.attendanceDeviceTypeRepo=attendanceDeviceTypeRepo;
//    }
//
//    public AttendanceDevice toEntity(AttendanceDevicePojo dto) {
//        AttendanceDevice entity = new AttendanceDevice();
//        return toEntity(dto, entity);
//    }
//
//    public AttendanceDevice toEntity(AttendanceDevicePojo dto, AttendanceDevice entity) {
//          entity.setDeviceModel(dto.getDeviceModel());
//          entity.setDeviceName(dto.getDeviceName());
//          entity.setDeviceSerialNo(dto.getDeviceSerialNo());
//          entity.setEffectDate(dto.getEffectDate());
//          entity.setIp(dto.getIp());
//          entity.setPort(dto.getPort());
//        entity.setId(dto.getId());
//        return entity;
//    }
//
//    public AttendanceDevicePojo toDto(AttendanceDevice entity) {
//        AttendanceDevicePojo dto = new AttendanceDevicePojo();
//        dto.setDeviceModel(entity.getDeviceModel());
//        dto.setDeviceName(entity.getDeviceName());
//        dto.setDeviceNo(entity.getDeviceNo());
//        dto.setDeviceSerialNo(entity.getDeviceSerialNo());
//        dto.setEffectDate(entity.getEffectDate());
//        dto.setIp(entity.getIp());
//        dto.setPort(entity.getPort());
//        dto.setSerialPort(entity.getSerialPort());
//        if (entity.getAttendanceDeviceTypes() != null) {
//            dto.setAttendanceDeviceTypes(entity.getAttendanceDeviceTypes().stream().map(AttendanceDeviceType::getId).collect(Collectors.toCollection(ArrayList::new)));
//        }
//        return dto;
//    }
//
//
//    public List<AttendanceDevicePojo> toDto(List<AttendanceDevice> entityList) {
//        List<AttendanceDevicePojo> attendanceDevicePojos = new ArrayList<>();
//        for (AttendanceDevice attendanceDeviceList : entityList) {
//            attendanceDevicePojos.add(toDto(attendanceDeviceList));
//        }
//        return attendanceDevicePojos;
//    }
//}