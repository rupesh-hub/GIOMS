package com.gerp.usermgmt.pojo.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.usermgmt.pojo.transfer.document.DocumentPojo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferSubmissionResponsePojo {

    private Long id;
    private String status;
    private String transferType;
    private DetailPojo fromOffice;
    private DetailPojo fromService;
    private DetailPojo fromSection;
    private DetailPojo fromPosition;
    private DetailPojo fromDesignation;
    private DetailPojo toOffice;
    private DetailPojo toService;
    private DetailPojo toSection;
    private DetailPojo toPosition;
    private DetailPojo toDesignation;
    private DetailPojo employeeToBeTransfer;
    private String expectedDepartureDateNp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expectedDepartureDateEn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedOn;
    private List<TransferRemarksPojo> remarks;
    private List<DocumentPojo> documents;
    private List<DetailPojo> sendToOffices;
}

