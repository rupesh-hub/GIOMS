package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceDateListPojo {


    private String pisCode;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkinTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkoutTime;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    private Boolean isActive=false;

}
