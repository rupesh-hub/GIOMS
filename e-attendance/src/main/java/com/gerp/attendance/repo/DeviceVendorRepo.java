package com.gerp.attendance.repo;

import com.gerp.attendance.model.device.DeviceVendor;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface DeviceVendorRepo extends GenericSoftDeleteRepository<DeviceVendor, Integer> {

    @Modifying
    @Query(value = "update attendance_device_vendor set is_active = not is_active where id = ?1", nativeQuery = true)
    void softDelete(Integer vendorId);
}
