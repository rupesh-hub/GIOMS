package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.enums.Gender;
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
@AllArgsConstructor
@NoArgsConstructor
public class HolidayResponsePojo {

    private Long id;
    private Integer holidayId;
    private String holidayNameEn;
    private String holidayNameNp;
    private Gender holidayFor;
    private String officeCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;
    private String fiscalYearCode;

    private Boolean isActive;
    private Boolean isSpecificHoliday;
    private String yearNp;

}
