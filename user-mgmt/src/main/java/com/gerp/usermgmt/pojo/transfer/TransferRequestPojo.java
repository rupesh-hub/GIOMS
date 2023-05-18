package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class TransferRequestPojo {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate requestedDateEn;
    private String requestedDateNp;
//    @NotNull
//    @NotEmpty
//    private String newPositionCode;
//    @NotNull
//    @NotEmpty
//    private String newDesignationCode;
    @NotNull
    @NotEmpty
    private List<String> newOfficeCodes;
//    private String newServiceCode;
    @NotNull
    private List<PreviousWorkDetailPojo> previousWorkDetail;
    private List<MultipartFile> document;
    private String remarks;
    private String requestedOfficeCode;
    @NotNull
    private List<ChecklistPojo> checklists;
    private Integer attendanceDays;

}
