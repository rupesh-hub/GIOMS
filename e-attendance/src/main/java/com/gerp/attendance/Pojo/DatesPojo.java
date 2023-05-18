package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatesPojo {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    private Long id;

    @JsonInclude
    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType durationType;
}
