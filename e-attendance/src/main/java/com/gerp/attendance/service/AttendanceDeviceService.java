package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.AttendanceDevicePojo;
import com.gerp.attendance.Pojo.AttendanceDeviceResponsePojo;
import com.gerp.attendance.Pojo.VendorRequestPojo;
import com.gerp.attendance.model.device.AttendanceDevice;
import com.gerp.attendance.model.device.DeviceVendor;
import com.gerp.shared.generic.api.GenericService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AttendanceDeviceService extends GenericService<AttendanceDevice, Integer> {

    /**
     * This method is used to save a attendance device.
     * @param attendanceDevicePojo
     * @return AttendanceDevice - Saved attendance device.
     */
    AttendanceDevice save(AttendanceDevicePojo attendanceDevicePojo);

    /**
     * This method updates the attendance device with id from received from the object param.
     * @param attendanceDevicePojo
     * @return AttendanceDevicePojo - Updated attendance device.
     */
    void update(AttendanceDevicePojo attendanceDevicePojo);

    /**
     *
     * @return AttendanceDevicePojo - List of all the attendance devices.
     */
    ArrayList<AttendanceDevicePojo> getAttendanceDevice();

    List<AttendanceDeviceResponsePojo> getAllAttendanceDevice();

    void deleteDevice(Integer deviceId);

   AttendanceDeviceResponsePojo getAttendanceDeviceById(Integer id);

    List<AttendanceDeviceResponsePojo> getAttendanceDeviceByOfficeCode(String officeCode);
}
