package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class OfficeTimePojo {
    private Integer id;

    private String officeCode;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime maximumLateCheckin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime maximumEarlyCheckout;

    private int allowedLimit;
}
