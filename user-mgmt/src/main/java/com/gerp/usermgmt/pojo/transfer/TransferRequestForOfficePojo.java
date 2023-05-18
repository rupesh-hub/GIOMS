package com.gerp.usermgmt.pojo.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TransferRequestForOfficePojo {

    private Long id;
    private List<DetailPojo> newOfficeCode;
    private DetailPojo oldOffice;
    private DetailPojo employee;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDateEn;
    private String requestedDateNp;
}
