package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeOnKaajPojo {

    private Long id;

    private String pisCode;

    private List<String> pisCodes;

    private String kaajTypeNameEn;

    private String KaajTypeNameNp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String officeCode;

    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType durationType;

    private String employeeNameEn;

    private String employeeNameNp;

    private Long noOfDays;

    public Long getNoOfDays() {
        return ChronoUnit.DAYS.between(this.getFromDateEn(), this.getToDateEn())+1;
    }
}
