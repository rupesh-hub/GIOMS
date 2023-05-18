package com.gerp.attendance.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface ValidationMapper {
    String validateLeave(@Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate,
                         @Param("pisCode") String pisCode,
                         @Param("officeCode") String officeCode,@Param("leaveId") Long leaveId,
                         @Param("year") String year,@Param("appliedForOthers") Boolean appliedForOthers
    );

    String validateSpecificHoliday(@Param("fromDate") LocalDate fromDate,@Param("toDate")  LocalDate toDate,
                                   @Param("pisCode") String pisCode,@Param("officeCode") String officeCode,
                                   @Param("appliedForOthers") Boolean appliedForOthers
    );

    String validateKaaj(@Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate,@Param("pisCode") String pisCode,@Param("officeCode") String officeCode,@Param("kaajId") Long kaajId,@Param("year") String year);
}
