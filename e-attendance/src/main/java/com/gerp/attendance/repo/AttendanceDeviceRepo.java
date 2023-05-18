package com.gerp.attendance.repo;

import com.gerp.attendance.model.device.AttendanceDevice;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AttendanceDeviceRepo extends GenericSoftDeleteRepository<AttendanceDevice, Integer> {


    @Query(value = "select  distinct ad.id as id,ad.device_name,ad.device_model,ad.device_no ,ad.device_serial_no ,ad.effect_date ,ad.port ,ad.ip ,ad.serial_port ,b.ALIAS from\n" +
            "    attendance_device ad,( select ad2.device_name ,ad2.device_model,string_agg(adt.attendance_device_type_en,',')ALIAS from attendance_device ad2\n" +
            "    left join attendance_device_device_type addt  on addt.attendance_device_id =ad2.id\n" +
            "    left join attendance_device_type adt  on addt.attendance_device_type_id =adt.id\n" +
            "    group by ad2.device_name ,ad2.device_model)b\n" +
            "    where ad.device_name=b.device_name order by id asc", nativeQuery = true)
    List<Map<String, Object>> findAttendanceDevice();


    @Modifying
    @Query(value = "UPDATE attendance_device set is_active =  is_active where id = ?1", nativeQuery = true)
    void softDeleteDevice(Integer deviceId);
}
