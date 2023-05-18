package com.gerp.attendance.Pojo.holiday;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author infodev
 * @version 1.0.0
 * @since 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HolidayCountDetailPojo {
    private Long totalHoliday;
    private List<PublicHolidayPojo> publicHolidays;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private List<LocalDate> weekends;
}
