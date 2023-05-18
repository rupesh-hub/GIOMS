package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.AttendanceDevicePojo;
import com.gerp.attendance.Pojo.AttendanceDeviceResponsePojo;
import com.gerp.attendance.mapper.AttendanceDeviceMapper;
import com.gerp.attendance.model.device.AttendanceDevice;
import com.gerp.attendance.repo.AttendanceDeviceRepo;
import com.gerp.attendance.repo.DeviceVendorRepo;
import com.gerp.attendance.service.AttendanceDeviceService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class AttendanceDeviceServiceImpl extends GenericServiceImpl<AttendanceDevice, Integer> implements AttendanceDeviceService {

    private AttendanceDeviceRepo attendanceDeviceRepo;
    private DeviceVendorRepo deviceVendorRepo;

    private AttendanceDeviceMapper attendanceDeviceMapper;

    private CustomMessageSource customMessageSource;

    @Autowired private TokenProcessorService tokenProcessorService;

    public AttendanceDeviceServiceImpl(AttendanceDeviceRepo attendanceDeviceRepo, DeviceVendorRepo deviceVendorRepo, AttendanceDeviceMapper attendanceDeviceMapper, CustomMessageSource customMessageSource) {
        super(attendanceDeviceRepo);
        this.attendanceDeviceRepo = attendanceDeviceRepo;
        this.deviceVendorRepo = deviceVendorRepo;
        this.attendanceDeviceMapper=attendanceDeviceMapper;
        this.customMessageSource = customMessageSource;
    }

    private AttendanceDevice attendanceDevicePojoToEntity(AttendanceDevicePojo attendanceDevicePojo) {
        AttendanceDevice attendanceDevice = new AttendanceDevice();
        attendanceDevice.setOfficeCode(tokenProcessorService.getOfficeCode());
        attendanceDevice.setDeviceName(attendanceDevicePojo.getDeviceName());
        attendanceDevice.setDeviceModel(attendanceDevicePojo.getDeviceModel());
        attendanceDevice.setDeviceSerialNo(attendanceDevicePojo.getDeviceSerialNo());
        attendanceDevice.setPort(attendanceDevicePojo.getPort());
        attendanceDevice.setIp(attendanceDevicePojo.getIp());
        attendanceDevice.setMacAddress(attendanceDevicePojo.getMacAddress());
        attendanceDevice.setDeviceVendor(deviceVendorRepo.getOne(attendanceDevicePojo.getVendorId()));
        return attendanceDevice;
    }

    @Override
    public AttendanceDevice findById(Integer uuid) {
        AttendanceDevice attendanceDevice = super.findById(uuid);
        if(attendanceDevice == null)
            throw new RuntimeException((customMessageSource.get("error.doesn't.exist", customMessageSource.get("AttendanceDevice"))));
        return attendanceDevice;
    }


    @Override
    public AttendanceDevice save(AttendanceDevicePojo attendanceDevicePojo) {
        AttendanceDevice attendanceDevice = new AttendanceDevice().builder()
                .officeCode(tokenProcessorService.getOfficeCode())
                .deviceName(attendanceDevicePojo.getDeviceName())
                .deviceModel(attendanceDevicePojo.getDeviceModel())
                .deviceSerialNo(attendanceDevicePojo.getDeviceSerialNo())
                .port(attendanceDevicePojo.getPort())
                .deviceMachineNo(attendanceDevicePojo.getDeviceMachineNo())
                .status(attendanceDevicePojo.getStatus())
                .macAddress(attendanceDevicePojo.getMacAddress())
                .deviceVendor(attendanceDevicePojo.getVendorId()==null?null:deviceVendorRepo.findById(attendanceDevicePojo.getVendorId()).get())
                .ip(attendanceDevicePojo.getIp()).build();
        return attendanceDeviceRepo.save(attendanceDevice);
    }

    @Override
    public void update(AttendanceDevicePojo attendanceDevicePojo) {
        AttendanceDevice update = attendanceDeviceRepo.findById(attendanceDevicePojo.getId()).orElseThrow(() -> new RuntimeException("No attendance device found"));
        AttendanceDevice attendanceDevice = attendanceDevicePojoToEntity(attendanceDevicePojo);

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, attendanceDevice);
        } catch (Exception e) {
            throw new RuntimeException("It does not exists");
        }
        attendanceDeviceRepo.save(update);
    }

//
//    @Override
//    public List<Map<String, Object>> findAll() {
//        List<Map<String, Object>> attendanceDeviceList = attendanceDeviceRepo.findAttendanceDevice();
//        return attendanceDeviceList;
//    }

    @Override
    public ArrayList<AttendanceDevicePojo> getAttendanceDevice() {
        return attendanceDeviceMapper.getAttendanceDevice();
    }

    @Override
    public List<AttendanceDeviceResponsePojo> getAllAttendanceDevice() {
        return attendanceDeviceMapper.getAllAttendanceDevices(tokenProcessorService.getOfficeCode());
    }

    @Override
    public void deleteDevice(Integer deviceId) {
        attendanceDeviceRepo.softDeleteDevice(deviceId);
    }

    @Override
    public AttendanceDeviceResponsePojo getAttendanceDeviceById(Integer id) {
        return attendanceDeviceMapper.getAttendanceDeviceById(id);
    }

    @Override
    public List<AttendanceDeviceResponsePojo> getAttendanceDeviceByOfficeCode(String officeCode) {
        return attendanceDeviceMapper.getAllAttendanceDevices(officeCode);
    }

}
