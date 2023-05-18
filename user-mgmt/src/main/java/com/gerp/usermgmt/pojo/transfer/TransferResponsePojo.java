package com.gerp.usermgmt.pojo.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentPojo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponsePojo {
    private Long id;
    private DetailPojo employee;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDateEn;
    private String requestedDateNp;
    private DetailPojo newPosition;
    private DetailPojo newService;
    private DetailPojo requestedOffice;
    private DetailPojo newDesignation;
    private List<PreviousWorkDetailResponsePojo> previousWorkDetail;
    private List<DetailPojo> newOfficeCode;
    private List<DocumentPojo> documents;
    private String remarks;
    private List<ChecklistResponsePojo> checklists;
    private String submittedDate;
    private String attendanceTotal;

}
