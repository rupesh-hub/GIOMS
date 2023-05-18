package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.gerp.attendance.Pojo.json.AttendanceStatusKeyValueOptionSerializer;
import com.gerp.shared.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeAttendancePojo {

    private Long id;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkIn;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkOut;

    private String pisCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String days;

    private String dateNp;

    @JsonSerialize(using = AttendanceStatusKeyValueOptionSerializer.class, as = Enum.class)
   private AttendanceStatus attendanceType;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime shiftCheckin;

    private Boolean remarks;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime shiftCheckout;

    private String officeCode;

    private String lateRemarks;

}
