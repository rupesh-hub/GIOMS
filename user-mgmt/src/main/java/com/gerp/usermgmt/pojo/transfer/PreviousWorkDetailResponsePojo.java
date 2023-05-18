package com.gerp.usermgmt.pojo.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class PreviousWorkDetailResponsePojo {
    private Long id;
    private DetailPojo oldPosition;
    private DetailPojo oldDesignation;
    private DetailPojo oldOffice;
    private DetailPojo oldService;
    private DetailPojo oldRegion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;
    private String fromDateNp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;
    private String toDateNp;
}
