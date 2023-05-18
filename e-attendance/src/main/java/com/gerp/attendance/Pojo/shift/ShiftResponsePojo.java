package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.attendance.Pojo.SetupPojo;
import com.gerp.shared.enums.EnglishDayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftResponsePojo {

    private Integer id;

    private EnglishDayType nameEn;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkinTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkoutTime;

    private Boolean isWeekend;

    private Boolean isActive;

    private SetupPojo setupPojo;
}
