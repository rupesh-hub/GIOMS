package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.RemainingLeaveResponsePoio;
import com.gerp.attendance.Pojo.VehicleCategoryPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface VehicleCategoryMapper {

    @Select("select  vc.id," +
            "vc.name_en," +
            "vc.name_np," +
            "vc.is_active" +
            " from vehicle_category vc;")
    ArrayList<VehicleCategoryPojo> getAllVehicleCategory();
}
