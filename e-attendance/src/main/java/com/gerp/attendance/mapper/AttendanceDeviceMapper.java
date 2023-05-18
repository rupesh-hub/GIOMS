package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.AttendanceDevicePojo;
import com.gerp.attendance.Pojo.AttendanceDeviceResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface AttendanceDeviceMapper {

    ArrayList<AttendanceDevicePojo> getAttendanceDevice();

    @Select("SELECT adv.name            AS vendorName,\n" +
            "       adv.id              as vendorId,\n" +
            "       ad.id               as id,\n" +
            "       ad.office_code      as officeCode,\n" +
            "       ad.device_name      AS deviceName,\n" +
            "       ad.device_model     as deviceModel,\n" +
            "       ad.device_serial_no as deviceSerialNo,\n" +
            "       ad.mac_address as macAddress,\n" +
            "       ad.port             as port,\n" +
            "       ad.ip               as ip,\n" +
            "       ad.is_active        as isActive\n" +
            "FROM attendance_device AS ad\n" +
            "         LEFT JOIN attendance_device_vendor AS adv ON ad.attendance_device_vendor_id = adv.id\n" +
            "where ad.office_code = #{officeCode} ")
    List<AttendanceDeviceResponsePojo> getAllAttendanceDevices(String officeCode);

    @Select("SELECT adv.name AS vendorName,ad.mac_address as macAddress, adv.id as vendorId, ad.id as id, ad.office_code as officeCode, ad.device_name AS deviceName, ad.device_model as deviceModel, ad.device_serial_no as deviceSerialNo, ad.port as port, ad.ip as ip,  ad.is_active as isActive FROM attendance_device AS ad LEFT JOIN attendance_device_vendor AS adv ON ad.attendance_device_vendor_id = adv.id WHERE ad.id = #{id}")
    AttendanceDeviceResponsePojo getAttendanceDeviceById(Integer id);

    @Select("SELECT adv.name AS vendorName, ad.mac_address as macAddress, adv.id as vendorId, ad.id as id, ad.office_code as officeCode, ad.device_name AS deviceName, ad.device_model as deviceModel, ad.device_serial_no as deviceSerialNo, ad.port as port, ad.ip as ip,  ad.is_active as isActive FROM attendance_device AS ad LEFT JOIN attendance_device_vendor AS adv ON ad.attendance_device_vendor_id = adv.id WHERE ad.attendance_device_vendor_id = #{vendorId}")
    List<AttendanceDeviceResponsePojo> getAllByVendorId(@Param("vendorId") Integer vendorId);

    @Select("SELECT adv.name AS vendorName,\n" +
            "       ad.mac_address as macAddress,\n" +
            "       ad.status as status,\n" +
            "       adv.id as vendorId,\n" +
            "       ad.id as id, \n" +
            "       ad.office_code as officeCode,\n" +
            "       ad.device_name AS deviceName,\n" +
            "       ad.device_model as deviceModel,\n" +
            "       ad.device_serial_no as deviceSerialNo,\n" +
            "       ad.port as port, ad.ip as ip,ad.is_active as isActive,\n" +
            "       ad.device_machine_no as deviceMachineNo\n" +
            "FROM attendance_device AS ad LEFT JOIN attendance_device_vendor\n" +
            "AS adv ON ad.attendance_device_vendor_id = adv.id WHERE ad.office_code =#{officeCode} and ad.is_active=true;")
   List<AttendanceDeviceResponsePojo> getByDeviceByOfficeCode(@Param("officeCode") String officeCode);


}
