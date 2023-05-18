package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.MonthlyAttendanceKeyValueOptionSerializer;
import com.gerp.shared.enums.MonthlyAttendanceStatus;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeAttendanceNewMonthlyPojo {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEn;

    private String dateNp;

    @JsonSerialize(using = MonthlyAttendanceKeyValueOptionSerializer.class, as = Enum.class)
    private MonthlyAttendanceStatus monthlyAttendanceStatus;

    private String holidayNameEn;

    private String holidayNameNp;
}
