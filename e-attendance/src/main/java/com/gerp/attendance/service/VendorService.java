package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.VendorRequestPojo;
import com.gerp.attendance.model.device.DeviceVendor;
import com.gerp.shared.generic.api.GenericService;

import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface VendorService extends GenericService<DeviceVendor, Integer> {

    DeviceVendor saveVendor(VendorRequestPojo vendorRequestPojo);

    ArrayList<VendorRequestPojo> getAllVendors();

    ArrayList<VendorRequestPojo> getAllActiveVendors();

    void deleteVendor(Integer vendorId);

    void updateVendor(VendorRequestPojo vendorRequestPojo);

}
