package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.shift.ShiftResponsePojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface ShiftDayConfigMapper {
    ArrayList<ShiftResponsePojo> getAllShiftDayConfig();

    ArrayList<ShiftResponsePojo> getShiftDayConfigByShift(Integer shiftId);

}
