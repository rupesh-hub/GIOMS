package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftTimePojo {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkinTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkoutTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime halfTime;

    private Boolean isMidNight;
}
