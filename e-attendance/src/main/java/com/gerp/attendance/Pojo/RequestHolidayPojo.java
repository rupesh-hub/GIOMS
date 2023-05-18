package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHolidayPojo extends RequestDayPojo {

    @NotNull
    private Long periodicHoliday;
}
