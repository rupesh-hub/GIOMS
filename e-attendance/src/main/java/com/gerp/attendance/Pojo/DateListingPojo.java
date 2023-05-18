package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateListingPojo {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateEn;

    private String startDateNp;

    private String endDateNp;
}
