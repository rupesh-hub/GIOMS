package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.shift.mapped.ShiftMappedPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Mapper
public interface ShiftMappingMapper {

    List<ShiftMappedPojo> getShiftMappedGroup(@Param("officeCode") String officeCode, @Param("ids") List<Long> ids, @Param("shiftId") Integer shiftId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    List<ShiftMappedPojo> getShiftMappedEmployee(@Param("officeCode") String officeCode, @Param("pisCodes") List<String> pisCodes, @Param("shiftId") Integer shiftId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

}
