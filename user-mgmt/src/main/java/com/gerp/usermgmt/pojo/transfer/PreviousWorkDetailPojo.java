package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class PreviousWorkDetailPojo {
   private Long id;
    @NotEmpty
    private String oldPositionCode;
    @NotNull
    @NotEmpty
    private String oldDesignationCode;
    @NotNull
    @NotEmpty
    private String oldOfficeCode;
    private String oldServiceCode;
    private String oldRegionCode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDateEn;
    private String fromDateNp;
   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDateEn;
    private String toDateNp;
}
