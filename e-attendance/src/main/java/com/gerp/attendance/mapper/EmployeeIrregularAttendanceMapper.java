package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.EmployeeIrregularAttendancePojo;
import com.gerp.attendance.Pojo.GayalKattiResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface EmployeeIrregularAttendanceMapper {
//    EmployeeIrregularAttendancePojo getIrregularAttendanceByPisCode(@Param("pisCode") String pisCode,
//                                                                    @Param("pisCode") String pisCode);

    EmployeeIrregularAttendancePojo getIrregularAttendance(@Param("pisCode") String pisCode,
                                                           @Param("shiftId") Long shiftId,
                                                           @Param("month") String month,
                                                           @Param("officeCode") String officeCode,
                                                           @Param("fiscalYear") String fiscalYear);

    void updateIrregularAttendance(@Param("id") Long id,
                                   @Param("latestUpdateDate") LocalDate latestUpdateDate,
                                    @Param("shiftCheckin") LocalTime shiftCheckin,
                                   @Param("shiftCheckout") LocalTime shiftCheckout,
                                   @Param("checkin") LocalTime checkin,
                                   @Param("checkout") LocalTime checkout,
                                   @Param("officeAllowedLimit") Integer officeAllowedLimit,
                                   @Param("officeLateLimit") LocalTime officeLateLimit,
                                   @Param("officeEarlyLimit") LocalTime officeEarlyLimit
                                  );

}
