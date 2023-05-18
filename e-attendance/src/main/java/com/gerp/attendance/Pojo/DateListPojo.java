package com.gerp.attendance.Pojo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DateListPojo {
    private LocalDate minDate;
    private LocalDate maxDate;
}
