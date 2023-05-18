package com.gerp.shared.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DatePojo {
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate fromDateEn;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;
}
