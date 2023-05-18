package com.gerp.attendance.Pojo.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceSearchPojo {
    private String pisCode;
    private Boolean isCheckedIn;
    private String officeCode;
    private LocalDate fromDateEn;
    private LocalDate toDateEn;
}
