package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.shift.OfficeTimePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface OfficeTimeConfigurationMapper {

    OfficeTimePojo getOfficeTimeByCode(@Param("officeCode") String officeCode);

    List<OfficeTimePojo> getAllOfficeTime(@Param("officeCode") List<String> officeCode);

}
