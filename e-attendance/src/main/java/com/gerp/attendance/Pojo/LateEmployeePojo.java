package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.utils.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LateEmployeePojo {

    private Long id;

    //    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkIn;

    //    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkOut;

    private String pisCode;
    private String employeeCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
    private AttendanceStatus attendanceType;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime shiftCheckin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime shiftCheckout;

    //    @JsonFormat(pattern = "HH:mm:ss")
    private String lateCheckin;
    private String earlyCheckOut;


    private String officeCode;

    private String lateRemarks;

    private String empNameEn;
    private String empNameNp;

    private String fdNameEn;
    private String fdNameNp;
    private boolean status = Boolean.TRUE;
    private int lateIn;
    private int lateInMax;

    private int earlyOutMax;
    private int earlyOut;

    private Long positionOrderNo;

//    public void setEarlyCheckOut(String earlyCheckOut) throws ParseException {
//        this.lateCheckin =
//                LocalTime.MIDNIGHT.plus(Duration.between(this.checkOut, this.shiftCheckout)).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//        ;
//    }

//    public void setLateCheckin(String lateCheckin) throws ParseException {
//        this.lateCheckin =
//                LocalTime.MIDNIGHT.plus(Duration.between(this.shiftCheckin, this.checkIn)).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//        ;
//    }
}
