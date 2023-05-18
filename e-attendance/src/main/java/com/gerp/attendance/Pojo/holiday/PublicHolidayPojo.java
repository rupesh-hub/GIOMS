package com.gerp.attendance.Pojo.holiday;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author infodev
 * @version 1.0.0
 * @since 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PublicHolidayPojo {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDate;
    private String nameEn;
    private String nameNp;
}
