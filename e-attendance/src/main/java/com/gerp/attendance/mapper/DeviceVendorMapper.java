package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.VendorRequestPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Mapper
public interface DeviceVendorMapper {

    @Select("SELECT id, code, name, is_active as isActive FROM attendance_device_vendor")
    ArrayList<VendorRequestPojo> getAllVendors();

    @Select("SELECT id, code, name, is_active as isActive FROM attendance_device_vendor WHERE is_active = true")
    ArrayList<VendorRequestPojo> getAllActiveVendors();
}
