package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayMapperPojo {

    private Integer id;

    private String officeCode;

    private String nameNp;

    private String nameEn;

    private String shortNameNp;

    private String shortNameEn;

    private String holidayFor;

    private Boolean isActive;

}
