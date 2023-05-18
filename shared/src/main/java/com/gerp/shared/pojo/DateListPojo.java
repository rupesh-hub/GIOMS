package com.gerp.shared.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DateListPojo {
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate minDate;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate maxDate;

    private int months;
}
