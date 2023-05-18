package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualAttendanceDetailPojo {

    private String pisCode;

    private String officeCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;

    //todo bulk
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;
    // todo bulk

    private Boolean isActive;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkinTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkoutTime;

}
