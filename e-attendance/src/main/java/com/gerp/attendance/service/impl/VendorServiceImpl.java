package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.AttendanceDeviceResponsePojo;
import com.gerp.attendance.Pojo.VendorRequestPojo;
import com.gerp.attendance.mapper.AttendanceDeviceMapper;
import com.gerp.attendance.mapper.DeviceVendorMapper;
import com.gerp.attendance.model.device.DeviceVendor;
import com.gerp.attendance.repo.DeviceVendorRepo;
import com.gerp.attendance.service.VendorService;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
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
public class VendorServiceImpl extends GenericServiceImpl<DeviceVendor, Integer> implements VendorService {

    private final DeviceVendorRepo deviceVendorRepo;
    private final DeviceVendorMapper deviceVendorMapper;
    private final AttendanceDeviceMapper attendanceDeviceMapper;

    public VendorServiceImpl(DeviceVendorRepo deviceVendorRepo,
                             DeviceVendorMapper deviceVendorMapper,
                             AttendanceDeviceMapper attendanceDeviceMapper) {
        super(deviceVendorRepo);
        this.deviceVendorRepo = deviceVendorRepo;
        this.deviceVendorMapper = deviceVendorMapper;
        this.attendanceDeviceMapper = attendanceDeviceMapper;
    }


    @Override
    public DeviceVendor saveVendor(VendorRequestPojo vendorRequestPojo) {
        DeviceVendor deviceVendor = new DeviceVendor();
        deviceVendor.setCode(vendorRequestPojo.getCode());
        deviceVendor.setName(vendorRequestPojo.getName());
        return deviceVendorRepo.save(deviceVendor);
    }

    @Override
    public ArrayList<VendorRequestPojo> getAllVendors() {
        return deviceVendorMapper.getAllVendors();
    }

    @Override
    public ArrayList<VendorRequestPojo> getAllActiveVendors() {
        return deviceVendorMapper.getAllActiveVendors();
    }

    @Override
    public void deleteVendor(Integer vendorId) {
        List<AttendanceDeviceResponsePojo> devices = attendanceDeviceMapper.getAllByVendorId(vendorId);
        deviceVendorRepo.softDelete(vendorId);
    }

    @Override
    public void updateVendor(VendorRequestPojo vendorRequestPojo) {
        DeviceVendor update = deviceVendorRepo.findById(vendorRequestPojo.getId()).orElseThrow(() -> new RuntimeException("No vendor found"));

        DeviceVendor deviceVendor = new DeviceVendor();
        deviceVendor.setName(vendorRequestPojo.getName());
        deviceVendor.setCode(vendorRequestPojo.getCode());
        deviceVendor.setActive(update.getActive());

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, deviceVendor);
        } catch (Exception e) {
            throw new RuntimeException("It doesn't exist");
        }
        deviceVendorRepo.save(update);
    }
}
