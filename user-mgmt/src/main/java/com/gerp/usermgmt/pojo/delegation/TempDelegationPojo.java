package com.gerp.usermgmt.pojo.delegation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class TempDelegationPojo {
    private Integer id;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime effectiveDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDate;
    private int fromSectionId;
    private String fromPisCode;

    public void setEffectiveDate(String effectiveDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(effectiveDate, formatter);
        this.effectiveDate = parse;
//                parse.atZone(ZoneId.of("Asia/Kathmandu")).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public void setExpireDate(String expireDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(expireDate, formatter);
        this.expireDate = parse;
//                parse.atZone(ZoneId.of("Asia/Kathmandu")).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private int toSectionId;
    private String toPisCode;
    private Boolean isReassignment;

    private Boolean isAbort;

}
