package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class TransferRequestToTransferPojo {
    @NotNull
    private Long transferId;
    @NotNull
    @NotEmpty
    private String toOfficeCode;
    @NotNull
    @NotEmpty
    private String toServiceCode;
    @NotNull
    @NotEmpty
    private String toPositionCode;
    @NotNull
    @NotEmpty
    private String toDesignationCode;
    private String toGroupCode;
    private String toSubGroupCode;
    private String expectedDepartureDateNp;
    private LocalDate expectedDepartureDateEn;
}
