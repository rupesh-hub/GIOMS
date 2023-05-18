package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class TransferPojo {

    private Long id;
    @NotNull
    @NotEmpty
    private String transferType;
    @NotNull
    @NotEmpty
    private String fromOfficeCode;
    private String fromServiceCode;
    private Long fromSectionCode;
    private String fromGroupCode;
    private String fromSubGroupCode;

    private String fromPositionCode;
    @NotNull
    @NotEmpty
    private String fromDesignationCode;
    @NotNull
    @NotEmpty
    private String toOfficeCode;
    private String toServiceCode;
    private String toPositionCode;
    private String toGroupCode;
    private String toSubGroupCode;
    private Long toSectionCode;
    @NotNull
    @NotEmpty
    private String toDesignationCode;
    private String expectedDepartureDateNp;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expectedDepartureDateEn;
    private String approverCode;
    private String employeePisCode;
    private String remarks;
    private List<MultipartFile> documents;

    public TransferPojo( String fromOfficeCode, String fromServiceCode,  String fromPositionCode,  String fromDesignationCode,
                         String toOfficeCode, String toServiceCode, String toPositionCode, String toGroupCode, String toSubGroupCode,
                         String toDesignationCode, String employeePisCode,String expectedDepartureDateNp,LocalDate expectedDepartureDateEn,String transferType) {
        this.fromOfficeCode = fromOfficeCode;
        this.fromServiceCode = fromServiceCode;
        this.fromPositionCode = fromPositionCode;
        this.fromDesignationCode = fromDesignationCode;
        this.toOfficeCode = toOfficeCode;
        this.toServiceCode = toServiceCode;
        this.toPositionCode = toPositionCode;
        this.toGroupCode = toGroupCode;
        this.toSubGroupCode = toSubGroupCode;
        this.toDesignationCode = toDesignationCode;
        this.employeePisCode = employeePisCode;
        this.expectedDepartureDateEn = expectedDepartureDateEn;
        this.expectedDepartureDateNp = expectedDepartureDateNp;
        this.transferType = transferType;
    }
}

