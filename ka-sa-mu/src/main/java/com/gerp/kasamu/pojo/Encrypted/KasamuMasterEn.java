package com.gerp.kasamu.pojo.Encrypted;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KasamuMasterEn {
    private String empShreni;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDate superReceivedDate;
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate superSubmittedDate;
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate reviewerSubDate;
    private boolean submittedStatus;
    private String valRemarksBySupervisor;
    private String valRemarksByEvaluator;
    private String valRemarksByEmployee;
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate evalSubDate;
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate evalCommitteeSubDate;
    private String currentOfficeCode;
    private Long regdNum;
    private String officeCode;
}
